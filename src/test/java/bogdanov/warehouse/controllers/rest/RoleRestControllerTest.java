package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.dto.ExceptionDTO;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.interfaces.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class RoleRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private RoleService service;

    private final String URI = "/api/console/roles";

    private List<RoleDTO> list = new LinkedList<>();
    private RoleDTO dto;

    @PostConstruct
    private void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAll() throws Exception {
        List<RoleDTO> all = service.getAll();
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<RoleDTO>>() {
        });
        assertEquals(all.size(), list.size());
        log.info(all.toString());
        log.info(list.toString());
        assertTrue(list.containsAll(all));
    }

    @Test
    void getByName() throws Exception {
        List<RoleDTO> all = service.getAll();
        for (RoleDTO role : all) {
            result = mvc.perform(get(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("name", role.getName()))
                    .andExpect(status().isOk())
                    .andReturn();
            assertEquals(role,
                    objectMapper.readValue(result.getResponse().getContentAsString(), RoleDTO.class));
        }
    }

    @Test
    void getByName_EmptyName() throws Exception {
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", Strings.EMPTY))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(),ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_ENTITY_NAME.name(), e.getType());
        assertEquals(ArgumentException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByName_SpaceName() throws Exception {
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", " "))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(),ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_ENTITY_NAME.name(), e.getType());
        assertEquals(ArgumentException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByName_BlankName() throws Exception {
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("name", "\t \t"))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(),ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_ENTITY_NAME.name(), e.getType());
        assertEquals(ArgumentException.class.getSimpleName(), e.getException());
    }

}