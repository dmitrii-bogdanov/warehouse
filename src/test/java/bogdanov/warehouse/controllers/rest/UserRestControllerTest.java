package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.dto.*;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.interfaces.UserService;
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
import javax.persistence.Embedded;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@AutoConfigureTestDatabase
class UserRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private UserService service;
    @Autowired
    private PersonService personService;
    @Autowired
    private UserAccountService accountService;

    private final String URI = "/api/users";

    private final String NAME = "USERNAME";
    private final String PASSWORD = "password";

    private List<UserDTO> users = new LinkedList<>();
    private List<PersonDTO> persons = new LinkedList<>();
    private List<UserDTO> list = new LinkedList<>();
    private UserDTO dto;

    private final Random generator = new Random(System.nanoTime());

    @PostConstruct
    private void init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        initData();
    }

    @BeforeEach
    private void clearBefore() {
        clear();
    }

    @AfterEach
    private void clear() {
        accountService.getAll().forEach(a -> accountService.delete(a.getId()));
        personService.getAll().forEach(p -> personService.delete(p.getId()));
    }

    private void initData() {
        persons = new LinkedList<>();
        for (int i = 0; i < Role.values().length; i++) {
            persons.add(
                    new PersonDTO(
                            null,
                            "firstname_" + (char) ('A' + i),
                            "lastname" + (char) ('A' + i),
                            null,
                            LocalDate.now().minusYears(20 + i),
                            null,
                            null,
                            "position" + (char) ('A' + i)));
        }
        persons = personService.add(persons);

        users = new LinkedList<>();
        UserAccountWithPasswordDTO dto;
        UserAccountDTO dtoUser;
        int i = 0;
        for (PersonDTO person : persons) {
            dto = new UserAccountWithPasswordDTO();
            dto.setUsername(NAME + "_" + i);
            dto.setPersonId(person.getId());
            dto.setPassword(PASSWORD + i);
            dto.setRoles(Collections.singletonList("ROLE_USER"));
            i++;
            dtoUser = accountService.add(dto);
            users.add(new UserDTO(dtoUser.getId(), dtoUser.getUsername(), dtoUser.getPersonId()));
        }
    }

    @Test
    void getAll() throws Exception {
        initData();
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserDTO>>() {
        });
        assertEquals(users.size(), list.size());
        assertTrue(list.containsAll(users));
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
    void getById() throws Exception {
        initData();
        for (UserDTO user : users) {
            result = mvc.perform(get(URI + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void getById_NotRegisteredId() throws Exception {
        initData();
        long id = getNotRecordedId(users.stream().map(UserDTO::getId).toList());
        result = mvc.perform(get(URI + "/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND.name(), e.getType());
        assertEquals(ResourceNotFoundException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByPersonId() throws Exception {
        initData();
        for (UserDTO user : users) {
            result = mvc.perform(get(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("personId", user.getPersonId().toString()))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void getByPersonId_NotRegisteredId() throws Exception {
        initData();
        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("personId", String.valueOf(id)))
                .andExpect(status().isNotFound())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND.name(), e.getType());
        assertEquals(ResourceNotFoundException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByUsername() throws Exception {
        initData();
        for (UserDTO user : users) {
            result = mvc.perform(get(URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", user.getUsername()))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void getByUsername_NotRecordedUsername() throws Exception {
        initData();
        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "something"))
                .andExpect(status().isNotFound())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND.name(), e.getType());
        assertEquals(ResourceNotFoundException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByUsername_EmptyUsername() throws Exception {
        initData();
        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", Strings.EMPTY))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_USERNAME.name(), e.getType());
        assertEquals(ArgumentException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByUsername_SpaceUsername() throws Exception {
        initData();
        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", " "))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_USERNAME.name(), e.getType());
        assertEquals(ArgumentException.class.getSimpleName(), e.getException());
    }

    @Test
    void getByUsername_BlankUsername() throws Exception {
        initData();
        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        result = mvc.perform(get(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .param("username", "\t \t"))
                .andExpect(status().isBadRequest())
                .andReturn();
        ExceptionDTO e = objectMapper.readValue(result.getResponse().getContentAsString(), ExceptionDTO.class);
        assertEquals(ExceptionType.BLANK_USERNAME.name(), e.getType());
        assertEquals(ArgumentException.class.getSimpleName(), e.getException());
    }


}