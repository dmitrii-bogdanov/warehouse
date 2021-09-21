package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService accountService;
    @Autowired
    private PersonService personService;
    @Autowired
    private UserRepository userRepository;

    private UserDTO dto;
    private UserDTO result;
    private List<UserDTO> users = new LinkedList<>();

    private List<PersonDTO> persons = new LinkedList<>();
    private List<UserAccountDTO> accounts = new LinkedList<>();
    private List<UserDTO> all = new LinkedList<>();

    private final String NULL_STR = null;
    private final String EMPTY_STR = Strings.EMPTY;
    private final String SPACE_STR = " ";
    private final String BLANK_STR = "\t \t\t \t";

    @BeforeEach
    private void clear() {
        accountService.getAll().forEach(account -> accountService.delete(account.getId()));
        accounts.clear();
        all.clear();
        users.clear();
    }

    private void createPersons() {
        if (persons.isEmpty()) {
            String firstname = "firstname";
            String lastname = "lastname";
            LocalDate date = LocalDate.of(2001, 1, 1);
            String position = "position";
            for (int i = 0; i < 3; i++) { // <=3
                persons.add(new PersonDTO(
                        null,
                        firstname + (char) ('A' + i),
                        lastname + (char) ('A' + i),
                        null,
                        date.plusYears(i).plusMonths(i).plusDays(i),
                        null,
                        null,
                        position + (char) ('A' + i)
                ));
            }
            persons = new LinkedList<>(personService.add(persons));
        }
    }

    private void createUsers() {
        createPersons();
        String username = "username";
        String password = "password";
        List<String> roleList = Arrays.stream(Role.values()).map(Role::name).toList();
        List<String> roles = new LinkedList<>();
        UserAccountWithPasswordDTO user;

        int i = 0;
        for (PersonDTO person : persons) {
            user = new UserAccountWithPasswordDTO();
            user.setUsername(username + i);
            user.setPassword(password + i);
            user.setPersonId(person.getId());
            roles.add(roleList.get(roleList.size() - 1 - i));
            user.setRoles(roles);
            accounts.add(accountService.add(user));
            i++;
        }
    }

    private UserDTO convert(UserAccountDTO account) {
        return new UserDTO(account.getId(), account.getUsername(), account.getPersonId());
    }

    private long getNotRecordedId(List<Long> ids) {
        Random generator = new Random(System.nanoTime());
        long id = generator.nextLong() & 1023;
        while (ids.contains(id)) {
            id = generator.nextLong() & 1023;
        }
        return id;
    }

    @Test
    void getAll() {
        assertTrue(userService.getAll().isEmpty());

        createUsers();
        users.addAll(accounts.stream().map(this::convert).toList());
        all = userService.getAll();
        assertEquals(users.size(), all.size());
        assertTrue(all.containsAll(users));

        while (!users.isEmpty()) {
            dto = users.get((int) (System.nanoTime() % users.size()));
            users.remove(dto);
            accountService.delete(dto.getId());
            all = userService.getAll();
            assertEquals(users.size(), all.size());
            assertTrue(all.containsAll(users));
            assertFalse(all.contains(dto));
        }

        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    void getById() {
        createUsers();
        users.addAll(accounts.stream().map(this::convert).toList());
        all = userService.getAll();

        for (UserDTO user : users) {
            assertEquals(user, userService.getById(user.getId()));
            assertEquals(all, userService.getAll());
        }
    }

    @Test
    void getById_NullId() {
        createUsers();
        all = userService.getAll();

        Long id = null;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getById_NotRecordedId() {
        createUsers();
        all = userService.getAll();

        long id = getNotRecordedId(accounts.stream().map(UserAccountDTO::getId).toList());
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.getById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getByUsername() {
        createUsers();
        users.addAll(accounts.stream().map(this::convert).toList());
        all = userService.getAll();

        for (UserDTO user : users) {
            assertEquals(user, userService.getByUsername(user.getUsername().toLowerCase(Locale.ROOT)));
            assertEquals(all, userService.getAll());
            assertEquals(user, userService.getByUsername(user.getUsername().toUpperCase(Locale.ROOT)));
            assertEquals(all, userService.getAll());
        }
    }

    @Test
    void getByUsername_NotRecordedUsername() {
        createUsers();
        all = userService.getAll();

        String username = accounts.get(0) + "some";
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.getByUsername(username));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getByUsername_NullUsername() {
        createUsers();
        all = userService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getByUsername(NULL_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getByUsername_EmptyUsername() {
        createUsers();
        all = userService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getByUsername(EMPTY_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getByUsername_SpaceUsername() {
        createUsers();
        all = userService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getByUsername(SPACE_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getByUsername_BlankUsername() {
        createUsers();
        all = userService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getByUsername(BLANK_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }


    @Test
    void getByPersonId() {
        createUsers();
        users.addAll(accounts.stream().map(this::convert).toList());
        all = userService.getAll();

        for (UserDTO user : users) {
            assertEquals(user, userService.getByPersonId(user.getPersonId()));
            assertEquals(all, userService.getAll());
        }
    }

    @Test
    void getByPersonId_NullId() {
        createUsers();
        all = userService.getAll();

        Long id = null;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userService.getByPersonId(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

    @Test
    void getByPersonId_NotRecordedId() {
        createUsers();
        all = userService.getAll();

        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.getByPersonId(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userService.getAll());
    }

}