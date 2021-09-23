package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.h2.engine.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class AccountRestControllerTest extends AbstractSpringBootTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private UserAccountService service;

    private final String URI = "/api/console/accounts";

    private final String FIRSTNAME = "FIRSTNAME";
    private final String LASTNAME = "LASTNAME";
    private final String POSITION = "position";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String ROLE = "ROLE_USER";

    private List<PersonDTO> persons = new LinkedList<>();
    private List<UserAccountDTO> users = new LinkedList<>();
    private List<UserAccountDTO> list = new LinkedList<>();
    private UserAccountDTO dto;

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
        personService.getAll().forEach(p -> personService.delete(p.getId()));
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
            users.add(service.add(dto));
        }
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
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserAccountDTO>>() {
        });
        assertEquals(users.size(), list.size());
        assertTrue(list.containsAll(users));
    }

    @Test
    void register() throws Exception {
        initData();
        service.getAll().forEach(u -> service.delete(u.getId()));
        UserAccountWithPasswordDTO dto = new UserAccountWithPasswordDTO();
        dto.setUsername(USERNAME);
        dto.setPersonId(persons.get(0).getId());
        dto.setRoles(Collections.singletonList(ROLE));
        dto.setPassword(PASSWORD);

        result = mvc.perform(post(URI).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();
        UserAccountDTO registered = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
        UserAccountDTO user = service.getByUsername(dto.getUsername());
        assertEquals(user, registered);
    }

    @Test
    void getById() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            result = mvc.perform(get(URI + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void getByUsername() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON)
                    .param("username", user.getUsername()))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void deleteById() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            result = mvc.perform(delete(URI + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void getAllByRole() throws Exception {
        initData();
        result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON)
                .param("role", ROLE))
                .andExpect(status().isOk())
                .andReturn();
        list = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserAccountDTO>>() {
        });
        assertEquals(users.size(), list.size());
        assertTrue(list.containsAll(users));
    }

    @Test
    void getByPersonId() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            result = mvc.perform(get(URI).contentType(MediaType.APPLICATION_JSON)
                    .param("personId", user.getPersonId().toString()))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void enable() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            result = mvc.perform(put(URI + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON)
                    .param("enable", Strings.EMPTY))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            user.setIsEnabled(true);
            assertEquals(user, dto);
        }
    }

    @Test
    void disable() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            service.enable(user.getId());
            result = mvc.perform(put(URI + "/" + user.getId()).contentType(MediaType.APPLICATION_JSON)
                    .param("disable", Strings.EMPTY))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            user.setIsEnabled(false);
            assertEquals(user, dto);
        }
    }

    @Test
    void updateRoles() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            List<String> roles = new LinkedList<>(user.getRoles());
            roles.add("ROLE_STAFF");
            user.setRoles(roles);
            result = mvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                    .param("roles", Strings.EMPTY)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void updateUsername() throws Exception {
        initData();
        int i = 0;
        for (UserAccountDTO user : users) {
            user.setUsername(user.getUsername() + i++);
            result = mvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                    .param("username", Strings.EMPTY)
                    .content(objectMapper.writeValueAsString(user)))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }

    @Test
    void updatePassword() throws Exception {
        initData();
        for (UserAccountDTO user : users) {
            UserAccountWithPasswordDTO passwordDTO = new UserAccountWithPasswordDTO();
            passwordDTO.setPassword(PASSWORD);
            passwordDTO.setUsername(user.getUsername());
            passwordDTO.setId(user.getId());
            result = mvc.perform(put(URI).contentType(MediaType.APPLICATION_JSON)
                    .param("password", Strings.EMPTY)
                    .content(objectMapper.writeValueAsString(passwordDTO)))
                    .andExpect(status().isOk())
                    .andReturn();
            dto = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountDTO.class);
            assertEquals(user, dto);
        }
    }


}