package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import org.apache.logging.log4j.util.Strings;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class UserAccountServiceTest {

    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private PersonService personService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private NomenclatureService nomenclatureService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private @Qualifier("user")
    BCryptPasswordEncoder userEncoder;
    @Autowired
    private @Qualifier("admin")
    BCryptPasswordEncoder adminEncoder;


    private List<PersonDTO> persons = new LinkedList<>();
    private UserAccountDTO dto;
    private UserAccountDTO result;
    private UserAccountWithPasswordDTO dtoWithPassword;
    private UserEntity entity;
    private UserEntity resultEntity;

    private final String USER = "user";
    private final String USERNAME = "username";
    private final String USER_ = "user_";
    private final String PASSWORD = "password";
    private final String NULL_STR = null;
    private final String EMPTY_STR = Strings.EMPTY;
    private final String SPACE_STR = " ";
    private final String BLANK_STR = "\t \t\t\t    \t \t\t  ";

    private List<RoleEntity> ROLES;
    private int PERSONS_LIST_SIZE;

    @BeforeEach
    private void clear() {
        recordRepository.deleteAll();
        nomenclatureService.getAll().forEach(n -> nomenclatureService.delete(n.getId()));
        userAccountService.getAll().forEach(a -> userAccountService.delete(a.getId()));
        personService.getAll().forEach(p -> personService.delete(p.getId()));

        ROLES = roleService.getAllEntities();
        PERSONS_LIST_SIZE = ROLES.size();

        persons.clear();
        dto = null;
        result = null;
        dtoWithPassword = null;
        entity = null;
        resultEntity = null;
    }

    @BeforeEach
    private void createPersons() {
        PersonDTO person;
        String firstname = "firstname";
        String lastname = "lastname";
        String position = "position";
        LocalDate date = LocalDate.of(2000, 1, 1);
        for (int i = 0; i < PERSONS_LIST_SIZE; i++) {
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
        personService.add(persons);
    }

    @Test
    void add() {
        List<UserAccountWithPasswordDTO> input = new LinkedList<>();
        List<UserAccountDTO> output = new LinkedList<>();
        List<String> rolesList = new LinkedList<>();

        final long id = 1L;
        final boolean TRUE = true;

        int i = 0;
        for (PersonDTO p : persons) {
            dtoWithPassword = new UserAccountWithPasswordDTO();
            dtoWithPassword.setId(id);
            dtoWithPassword.setUsername(USER_ + i);
            dtoWithPassword.setPassword(PASSWORD + i);
            dtoWithPassword.setPersonId(p.getId());
            dtoWithPassword.setIsEnabled(TRUE);
            rolesList.add(ROLES.get(i++).getName());
            dtoWithPassword.setRoles(new LinkedList<>(rolesList));
            input.add(dtoWithPassword);
        }
        assertTrue(userAccountService.getAll().isEmpty());
        input.forEach(account -> output.add(userAccountService.add(account)));

        assertEquals(input.size(), PERSONS_LIST_SIZE);
        assertEquals(input.size(), output.size());
        assertEquals(output.size(), userAccountService.getAll().size());
        assertTrue(userAccountService.getAll().containsAll(output));

        Set<Long> ids = new HashSet<>();
        output.forEach(a -> ids.add(a.getId()));
        assertEquals(input.size(), ids.size());

        for (i = 0; i < input.size(); i++) {

            assertNotNull(output.get(i));
            assertTrue(output.get(i).getId() > 0);
            assertEquals(input.get(i).getUsername().toUpperCase(Locale.ROOT), output.get(i).getUsername());
            assertTrue(userEncoder.matches(input.get(i).getPassword(),
                    userAccountService.getEntityById(output.get(i).getId()).getPassword()));
            assertEquals(input.get(i).getRoles().size(), output.get(i).getRoles().size());
            assertTrue(output.get(i).getRoles().containsAll(input.get(i).getRoles()));
            assertTrue(
                    (input.get(i).getRoles().contains(Role.ROLE_ADMIN.name())
                            && !adminEncoder.upgradeEncoding(
                            userAccountService.getEntityById(output.get(i).getId()).getPassword()))
                            || (!input.get(i).getRoles().contains(Role.ROLE_ADMIN.name())
                            && userEncoder.upgradeEncoding(
                            userAccountService.getEntityById(output.get(i).getId()).getPassword()))
            );
            assertNotNull(output.get(i).getPersonId());
            assertEquals(input.get(i).getPersonId(), output.get(i).getPersonId());
            assertFalse(output.get(i).getIsEnabled());
        }

    }

    @Test
    void add_NullDto() {
        dtoWithPassword = null;
        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_NotValidPassword() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        String password = PASSWORD.substring(0, 8);
        assertTrue(password.length() < 8);

        assertTrue(userAccountService.getAll().isEmpty());
        dtoWithPassword.setPassword(password);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_NullPassword() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        String password = NULL_STR;

        dtoWithPassword.setPassword(password);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_EmptyPassword() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        String password = EMPTY_STR;

        dtoWithPassword.setPassword(password);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_SpacePassword() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        String password = SPACE_STR;

        dtoWithPassword.setPassword(password);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_BlankPassword() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        String password = BLANK_STR;

        dtoWithPassword.setPassword(password);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_MinimalValidPassword() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        assertTrue(userAccountService.getAll().isEmpty());

        dtoWithPassword.setPassword(PASSWORD);

        assertTrue(userAccountService.getAll().isEmpty());
        assertDoesNotThrow(() -> userAccountService.add(dtoWithPassword));
        assertFalse(userAccountService.getAll().isEmpty());
    }

    private UserAccountWithPasswordDTO getDtoWithPassword() {
        UserAccountWithPasswordDTO dto = new UserAccountWithPasswordDTO();
        dto = new UserAccountWithPasswordDTO();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        dto.setRoles(ROLES.stream().map(RoleEntity::getName).toList());
        dto.setPersonId(persons.get(0).getId());
        return dto;
    }

    @Test
    void add_NullUsername() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setUsername(NULL_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_EmptyUsername() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setUsername(EMPTY_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_SpaceUsername() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setUsername(SPACE_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_BlankUsername() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setUsername(BLANK_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_NullRoles() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setRoles(null);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_EmptyRoles() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setRoles(Collections.emptyList());

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_NullRoleName() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.getRoles().add(NULL_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_EmptyRoleName() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.getRoles().add(EMPTY_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_SpaceRoleName() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.getRoles().add(SPACE_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_BlankRoleName() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.getRoles().add(BLANK_STR);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_IncorrectRoleName() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();

        final String name = "ROLE_SOMETHING";
        assertTrue(ROLES.stream().filter(r -> r.getName().equalsIgnoreCase(name)).toList().isEmpty());

        dtoWithPassword.getRoles().add(name);

        assertTrue(userAccountService.getAll().isEmpty());
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_NullPersonId() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        dtoWithPassword.setPersonId(null);

        assertTrue(userAccountService.getAll().isEmpty());
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_NotExistingPersonId() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();

        Random generator = new Random(System.nanoTime());
        long id = generator.nextLong();
        int i = 0;
        while (persons.stream().map(p -> p.getId()).toList().contains(id)) {
            id = generator.nextLong();
            if (i++ > 1000) {
                throw new RuntimeException("Some problem in cycle");
            }
        }
        dtoWithPassword.setPersonId(id);

        assertTrue(userAccountService.getAll().isEmpty());
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void add_AlreadyRegisteredPerson() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        result = userAccountService.add(dtoWithPassword);
        assertTrue(userAccountService.getAll().contains(result));

        dtoWithPassword.setUsername(dtoWithPassword.getUsername() + "something");

        //TODO find out exception
        userAccountService.add(dtoWithPassword);
    }

    @Test
    void add_AlreadyRegisteredUsername() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        result = userAccountService.add(dtoWithPassword);
        assertTrue(userAccountService.getAll().contains(result));

        dtoWithPassword.setUsername(dtoWithPassword.getUsername());
        dtoWithPassword.setPersonId(persons.get(1).getId());

        //TODO find out exception
        userAccountService.add(dtoWithPassword);
    }

}
















