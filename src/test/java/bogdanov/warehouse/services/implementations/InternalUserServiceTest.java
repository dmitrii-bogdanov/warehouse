package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.RoleService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class InternalUserServiceTest {

    @Autowired
    private InternalUserService userService;
    @Autowired
    private UserAccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PersonService personService;

    private String USERNAME = "username";
    private String PASSWORD = "password";
    private String NULL_STR = null;
    private String EMPTY_STR = Strings.EMPTY;
    private String SPACE_STR = " ";
    private String BLANK_STR = "\t \t\t\t   \t \t\t ";

    private List<PersonDTO> persons = new LinkedList<>();
    private List<RoleEntity> roles = new LinkedList<>();

    @BeforeEach
    private void clear() {
        accountService.getAll().forEach(account -> accountService.delete(account.getId()));
    }

    private void readRoles() {
        roles = roleService.getAllEntities();
    }

    private void createPersons() {
        readRoles();
        PersonDTO person;
        String firstname = "firstname";
        String lastname = "lastname";
        String position = "position";
        LocalDate date = LocalDate.of(2000, 1, 1);
        for (int i = 0; i < roles.size(); i++) {
            person = new PersonDTO(
                    null,
                    firstname + i,
                    lastname + i,
                    null,
                    date.plusYears(i),
                    null,
                    null,
                    position + i
            );
            persons.add(person);
        }
        persons = new LinkedList<>(personService.add(persons));
    }

    private List<UserAccountDTO> createUsers() {
        createPersons();
        List<UserAccountDTO> list = new LinkedList<>();
        UserAccountWithPasswordDTO dto;
        for (int i = 0; i < persons.size(); i++) {
            dto = new UserAccountWithPasswordDTO();
            dto.setUsername(USERNAME + i);
            dto.setPassword(PASSWORD + i);
            dto.setPersonId(persons.get(i).getId());
            dto.setRoles(Collections.singleton(roles.get(i).getName()));
            list.add(accountService.add(dto));
        }
        return list;
    }

    @Test
    void getEntityByUsername() {
        List<UserAccountDTO> users = createUsers();
        UserEntity entity;
        for (UserAccountDTO user : users) {
            entity = userService.getEntityByUsername(user.getUsername().toLowerCase(Locale.ROOT));
            assertNotNull(entity);
            assertEquals(user.getId(), entity.getId());
            entity = null;
        }
    }

    @Test
    void getEntityByUsername_NotRecordedUsername() {
        List<UserAccountDTO> users = createUsers();
        assertFalse(users.stream().map(UserAccountDTO::getUsername).toList().contains(USERNAME));
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.getEntityByUsername(USERNAME));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getEntityByUsername_NullUsername() {
        String username = NULL_STR;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getEntityByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
    }

    @Test
    void getEntityByUsername_EmptyUsername() {
        String username = EMPTY_STR;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getEntityByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
    }

    @Test
    void getEntityByUsername_SpaceUsername() {
        String username = SPACE_STR;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getEntityByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
    }

    @Test
    void getEntityByUsername_BlankUsername() {
        String username = BLANK_STR;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getEntityByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
    }

}