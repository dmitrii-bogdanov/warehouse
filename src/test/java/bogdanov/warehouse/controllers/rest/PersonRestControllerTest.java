package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.dto.search.SearchPersonDTO;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.interfaces.UserService;
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
import java.util.*;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class PersonRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private PersonService service;
    @Autowired
    private PositionService positionService;

    private final String URI = "/api/persons";

    private final String FIRSTNAME = "FIRSTNAME";
    private final String LASTNAME = "LASTNAME";
    private final String POSITION = "position";

    private List<PersonDTO> persons = new LinkedList<>();
    private List<PersonDTO> list = new LinkedList<>();
    private PersonDTO dto;

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
        service.getAll().forEach(p -> service.delete(p.getId()));
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
        persons = service.add(persons);
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
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonDTO>>() {
        });
        assertEquals(persons.size(), list.size());
        assertTrue(list.containsAll(persons));
    }

    @Test
    void getById() throws Exception {
        initData();
        for (PersonDTO person : persons) {
            result = mvc.perform(get(URI + "/" + person.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), PersonDTO.class);
            assertEquals(person, dto);
        }
    }

    @Test
    void add() throws Exception {
        dto = new PersonDTO();
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setBirth(LocalDate.now().minusYears(20));
        dto.setPosition(POSITION);

        result = mvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(dto))))
                .andExpect(status().isOk())
                .andReturn();
        dto = service.getAll().get(0);
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonDTO>>() {
        });
        assertEquals(1, list.size());
        assertTrue(list.contains(dto));
    }

    @Test
    void deleteById() throws Exception {
        initData();
        for (PersonDTO person : persons) {
            result = mvc.perform(delete(URI + "/" + person.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), PersonDTO.class);
            assertEquals(person, dto);
        }
    }

    @Test
    void update() throws Exception {
        initData();
        int i = 0;
        persons.get(0).setFirstname(FIRSTNAME);
        result = mvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(persons.get(0)))))
                .andExpect(status().isOk())
                .andReturn();
        dto = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonDTO>>() {
        }).get(0);
        assertEquals(persons.get(0), dto);
    }

    @Test
    void getByPositionId() throws Exception {
        initData();
        long id;
        for (PersonDTO person : persons) {
            id = positionService.getByName(person.getPosition()).getId();
            result = mvc.perform(get(URI + "/position/" + id).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonDTO>>() {
            });
            assertEquals(1, list.size());
            assertTrue(list.contains(person));
        }
    }

    @Test
    void getByPositionName() throws Exception {
        initData();
        long id;
        for (PersonDTO person : persons) {
            id = positionService.getByName(person.getPosition()).getId();
            result = mvc.perform(get(URI + "/position").contentType(MediaType.APPLICATION_JSON)
                    .param("name", person.getPosition()))
                    .andExpect(status().isOk())
                    .andReturn();
            list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonDTO>>() {
            });
            assertEquals(1, list.size());
            assertTrue(list.contains(person));
        }
    }

    @Test
    void search() throws Exception {
        initData();
        SearchPersonDTO searchPersonDTO = new SearchPersonDTO();
        searchPersonDTO.setFirstname(FIRSTNAME);
        result = mvc.perform(get(URI + "/search").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchPersonDTO)))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PersonDTO>>() {
        });
        assertEquals(persons.size(), list.size());
        assertTrue(list.containsAll(persons));
    }

}