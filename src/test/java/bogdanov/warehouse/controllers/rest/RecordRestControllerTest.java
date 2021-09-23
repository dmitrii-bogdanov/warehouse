package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.dto.*;
import bogdanov.warehouse.dto.search.SearchRecordDTO;
import bogdanov.warehouse.services.interfaces.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class RecordRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private RecordService service;
    @Autowired
    private RecordRepository repository;
    @Autowired
    private ReverseRecordRepository reverseRepository;
    @Autowired
    private PersonService personService;
    @Autowired
    private UserAccountService accountService;
    @Autowired
    private NomenclatureService nomenclatureService;
    @Autowired
    private NomenclatureRepository nomenclatureRepository;

    private final String URI = "/api/records";

    private final String FIRSTNAME = "FIRSTNAME";
    private final String NOMENCLATURE = "NOMENCLATURE";
    private final String LASTNAME = "LASTNAME";
    private final String POSITION = "position";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String ROLE = "ROLE_USER";
    private final String RECEPTION = RecordType.RECEPTION.name();
    private final String RELEASE = RecordType.RELEASE.name();

    private List<RecordDTO> records = new LinkedList<>();
    private List<PersonDTO> persons = new LinkedList<>();
    private List<UserAccountDTO> users = new LinkedList<>();
    private List<NomenclatureDTO> nomenclature = new LinkedList<>();
    private List<RecordDTO> list = new LinkedList<>();
    private RecordDTO dto;

    private final Random generator = new Random(System.nanoTime());

    @PostConstruct
    private void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    private void clearBefore() {
        clear();
    }

    @AfterEach
    private void clear() {
        reverseRepository.deleteAll();
        repository.deleteAll();
        accountService.getAll().forEach(p -> accountService.delete(p.getId()));
        personService.getAll().forEach(p -> personService.delete(p.getId()));
        nomenclatureRepository.deleteAll();
    }

    private void initData() {
        persons = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            persons.add(
                    new PersonDTO(
                            null,
                            "firstname_" + (char) ('A' + i),
                            "lastname" + (char) ('A' + i),
                            "patronymic" + (char) ('A' + i),
                            LocalDate.now().minusYears(20 + i),
                            "+8812" + i,
                            (char) ('A' + i) + i + "@domain.com",
                            "position" + (char) ('A' + i)));
        }
        persons = personService.add(persons);

        UserAccountWithPasswordDTO dto;
        int i = 0;
        for (PersonDTO person : persons) {
            dto = new UserAccountWithPasswordDTO();
            dto.setRoles(Collections.singletonList(ROLE));
            dto.setPassword(PASSWORD + i);
            dto.setPersonId(person.getId());
            dto.setUsername(USERNAME + "_" + i);
            i++;
            users.add(accountService.add(dto));
        }

        NomenclatureDTO nomenclatureDTO;
        for (i = 0; i < users.size(); i++) {
            nomenclatureDTO = new NomenclatureDTO(null, NOMENCLATURE + i, null, null);
            nomenclature.add(nomenclatureDTO);
        }
        nomenclature = new LinkedList<>(nomenclatureService.createNew(nomenclature));
    }

    private void initRecords() {
        int i = 0;
        for (NomenclatureDTO n : nomenclature) {
            dto = new RecordDTO();
            dto.setNomenclatureId(n.getId());
            dto.setType(RECEPTION);
            dto.setAmount(5L);
            records.add(service.add(dto, users.get(i++).getUsername()));
        }
    }

    private List<RecordDTO> formatTime(List<RecordDTO> records) {
        return records.stream().map(this::formatTime).toList();
    }

    private RecordDTO formatTime(RecordDTO dto) {
        dto.setTime(formatTime(dto.getTime()));
        return dto;
    }

    private LocalDateTime formatTime(LocalDateTime time) {
        LocalDateTime tmp = time;
        int nano = tmp.getNano();
        nano = (nano / 1000 + (nano % 1000 >= 500 ? 1 : 0)) * 1000;
        boolean plusSecond = nano / 1_000_000_000 > 0;
        nano = nano % 1_000_000_000;
        tmp = LocalDateTime.of(tmp.getYear(), tmp.getMonthValue(), tmp.getDayOfMonth(), tmp.getHour(), tmp.getMinute(), tmp.getSecond(), nano);
        if (plusSecond) {
            tmp = tmp.plusSeconds(1);
        }
        return tmp;
    }

    private long getNotRecordedId(List<Long> list) {
        Random generator = new Random(System.nanoTime());
        long id = generator.nextLong() & 1023;
        while (list.contains(id)) {
            id = generator.nextLong() & 1023;
        }
        return id;
    }

    @Test
    void getAll() throws Exception {
        initData();
        initRecords();
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RecordDTO>>() {
        });
        assertEquals(records.size(), list.size());
        assertTrue(list.containsAll(formatTime(records)));
    }

    @Test
    void getById() throws Exception {
        initData();
        initRecords();
        for (RecordDTO record : records) {
            result = mvc.perform(get(URI + "/" + record.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), RecordDTO.class);
            assertEquals(formatTime(record), dto);
        }
    }

    @Test
    void search() throws Exception {
        initData();
        initRecords();
        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        result = mvc.perform(get(URI + "/search").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RecordDTO>>() {
        });
        assertEquals(records.size(), list.size());
        assertTrue(list.containsAll(formatTime(records)));
    }


}