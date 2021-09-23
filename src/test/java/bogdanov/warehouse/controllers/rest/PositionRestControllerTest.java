package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.ExceptionDTO;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
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
import java.util.*;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class PositionRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private PositionService service;
    @Autowired
    private PersonService personService;

    private final String URI = "/api/positions";

    private final String NAME = "POSITION";

    private List<PositionDTO> positions = new LinkedList<>();
    private List<PositionDTO> list = new LinkedList<>();
    private PositionDTO dto;

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
        positions = new LinkedList<>();
        dto = null;
    }

    private void initData() {
        for (int i = 0; i < 3; i++) {
            positions.add(service.add(new PositionDTO(null, NAME + "_" + i)));
        }
    }

    @Test
    void getAll() throws Exception {
        initData();
        result = mvc.perform(get(URI).content(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PositionDTO>>() {
        });
        assertEquals(positions.size(), list.size());
        assertTrue(list.containsAll(positions));
    }

    @Test
    void add() throws Exception {
        dto = new PositionDTO(null, NAME);
        result = mvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(dto))))
                .andExpect(status().isOk())
                .andReturn();
        dto = service.getByName(dto.getName());
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PositionDTO>>() {
        });
        assertEquals(1, list.size());
        assertTrue(list.contains(dto));

        PositionDTO anotherDto = new PositionDTO(null, NAME);
        result = mvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(anotherDto))))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PositionDTO>>() {
        });
        assertEquals(1, list.size());
        assertTrue(list.contains(dto));
    }

    @Test
    void add_Blank() throws Exception {
        dto = new PositionDTO(null, " \t");
        result = mvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singleton(dto))))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO exceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_ENTITY_NAME.name(), exceptionDTO.getType());
        assertEquals(ArgumentException.class.getSimpleName(), exceptionDTO.getException());
    }

    @Test
    void deleteById() throws Exception {
        initData();
        for (PositionDTO position : positions) {
            result = mvc.perform(delete(URI + "/" + position.getId()).content(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), PositionDTO.class);
            assertEquals(position, dto);
        }
    }

    @Test
    void getById() throws Exception {
        initData();
        for (PositionDTO position : positions) {
            result = mvc.perform(get(URI + "/" + position.getId()).content(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), PositionDTO.class);
            assertEquals(position, dto);
        }
    }

    @Test
    void getByName() throws Exception {
        initData();
        for (PositionDTO position : positions) {
            result = mvc.perform(get(URI)
                    .content(MediaType.APPLICATION_JSON_VALUE)
                    .param("name", position.getName()))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), PositionDTO.class);
            assertEquals(position, dto);
        }
    }

    @Test
    void search() throws Exception {
        initData();
        result = mvc.perform(get(URI + "/search")
                .content(MediaType.APPLICATION_JSON_VALUE)
                .param("name", NAME))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<PositionDTO>>() {
        });
        assertEquals(positions.size(), list.size());
        assertTrue(list.containsAll(positions));
    }

}