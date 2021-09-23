package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.ExceptionDTO;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class NomenclatureRestControllerTest extends AbstractSpringBootTest {
    @Autowired
    private NomenclatureService service;
    @Autowired
    private NomenclatureRepository repository;

    private final String URI = "/api/nomenclature";

    private final String NAME = "NOMENCLATURE_NAME";
    private final String CODE = "CODE";

    private List<NomenclatureDTO> nomenclature = new LinkedList<>();
    private List<NomenclatureDTO> list = new LinkedList<>();
    private NomenclatureDTO dto;

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
        repository.deleteAll();
        nomenclature = new LinkedList<>();
        dto = null;
    }

    private void initData() {
        for (int i = 0; i < 3; i++) {
            nomenclature.add(new NomenclatureDTO(
                    null,
                    NAME + "_" + i,
                    CODE + "_" + i,
                    null
            ));
        }
        nomenclature = new LinkedList<>(service.createNew(nomenclature));
    }

    private NomenclatureDTO add(NomenclatureDTO dto, long amount) {
        dto.setAmount(amount);
        return service.addAmount(dto);
    }

    private long getNotRecordedId(List<Long> list) {
        long id = generator.nextLong() & 1023;
        while (list.contains(id)) {
            id = generator.nextLong() & 1023;
        }
        return id;
    }

    @Test
    void getAll() throws Exception {
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<NomenclatureDTO>>() {
        });
        assertTrue(list.isEmpty());

        initData();
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<NomenclatureDTO>>() {
        });
        assertEquals(nomenclature.size(), list.size());
        assertTrue(list.containsAll(nomenclature));
    }

    @Test
    void getById() throws Exception {
        initData();
        for (NomenclatureDTO n : nomenclature) {
            result = mvc.perform(get(URI + "/" + n.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(
                    n,
                    objectMapper.readValue(result.getResponse().getContentAsString(), NomenclatureDTO.class)
            );
        }
    }

    @Test
    void getById_NotRecordedId() throws Exception {
        initData();
        long id = getNotRecordedId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        result = mvc.perform(get(URI + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND.name(), dto.getType());
        assertEquals(ResourceNotFoundException.class.getSimpleName(), dto.getException());
    }

    @Test
    void getAllAvailable() throws Exception {
        initData();
        dto = nomenclature.get(nomenclature.size() - 1);
        nomenclature.remove(dto);
        dto = add(dto, 10L);

        result = mvc.perform(get(URI + "/available").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<NomenclatureDTO>>() {
        });

        assertEquals(1, list.size());
        assertTrue(list.contains(dto));
        assertFalse(list.containsAll(nomenclature));
    }

    @Test
    void getByName() throws Exception {
        initData();
        for (NomenclatureDTO n : nomenclature) {
            result = mvc.perform(get(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("name", n.getName()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(
                    n,
                    objectMapper.readValue(result.getResponse().getContentAsString(), NomenclatureDTO.class)
            );
        }
    }

    @Test
    void getByName_NotRecordedName() throws Exception {
        initData();
        long id = getNotRecordedId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "something"))
                .andExpect(status().isNotFound())
                .andReturn();

        ExceptionDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND.name(), dto.getType());
        assertEquals(ResourceNotFoundException.class.getSimpleName(), dto.getException());
    }

    @Test
    void getByName_EmptyName() throws Exception {
        initData();
        long id = getNotRecordedId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", Strings.EMPTY))
                .andExpect(status().isBadRequest())
                .andReturn();

        ExceptionDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_NAME.name(), dto.getType());
        assertEquals(ArgumentException.class.getSimpleName(), dto.getException());
    }

    @Test
    void getByName_SpaceName() throws Exception {
        initData();
        long id = getNotRecordedId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", " "))
                .andExpect(status().isBadRequest())
                .andReturn();

        ExceptionDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_NAME.name(), dto.getType());
        assertEquals(ArgumentException.class.getSimpleName(), dto.getException());
    }

    @Test
    void getByName_BlankName() throws Exception {
        initData();
        long id = getNotRecordedId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", " \t \t "))
                .andExpect(status().isBadRequest())
                .andReturn();

        ExceptionDTO dto = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_NAME.name(), dto.getType());
        assertEquals(ArgumentException.class.getSimpleName(), dto.getException());
    }

    @Test
    void createNew() throws Exception {
        dto = new NomenclatureDTO(null, NAME, null, null);
        result = mvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(dto))))
                .andExpect(status().isOk())
                .andReturn();

        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<NomenclatureDTO>>() {
        });
        dto = service.getAll().get(0);
        assertEquals(1, list.size());
        assertTrue(list.contains(dto));
    }

    @Test
    void getByCode() throws Exception {
        initData();
        for (NomenclatureDTO n : nomenclature) {
            result = mvc.perform(get(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("code", n.getCode()))
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(
                    n,
                    objectMapper.readValue(result.getResponse().getContentAsString(), NomenclatureDTO.class)
            );
        }
    }

    @Test
    void update() throws Exception {
        initData();
        dto = new NomenclatureDTO(nomenclature.get(0).getId(), NAME, null, null);
        result = mvc.perform(put(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(dto))))
                .andExpect(status().isOk())
                .andReturn();

        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<NomenclatureDTO>>() {
        });
        dto = service.getById(dto.getId());
        assertEquals(1, list.size());
        assertTrue(list.contains(dto));
    }

    @Test
    void search() throws Exception {
        List<NomenclatureDTO> all = service.getAll();
        result = mvc.perform(get(URI + "/search")
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", NAME)
                .param("code", "")
                .param("minAmount", "")
                .param("maxAmount", "5"))
                .andExpect(status().isOk())
                .andReturn();

        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<NomenclatureDTO>>() {
        });
        assertEquals(all.size(), list.size());
        assertTrue(list.containsAll(all));
    }

}