package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.dto.*;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ProhibitedRemovingException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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
    private NomenclatureRepository nomenclatureRepository;
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
    private List<UserAccountDTO> users;
    private List<UserAccountDTO> all;

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
        nomenclatureRepository.deleteAll();
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
        users = null;
        all = null;
    }

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
        persons = new LinkedList<>(personService.add(persons));
    }

    private UserAccountWithPasswordDTO getDtoWithPassword() {
        return getDtoWithPassword(null);
    }

    private UserAccountWithPasswordDTO getDtoWithPassword(Long num) {
        UserAccountWithPasswordDTO tempDto = new UserAccountWithPasswordDTO();
        tempDto = new UserAccountWithPasswordDTO();
        tempDto.setUsername(USERNAME);
        tempDto.setPassword(PASSWORD + (num == null ? EMPTY_STR : num));
        tempDto.setRoles(new LinkedList<>(ROLES.stream().map(RoleEntity::getName).toList()));
        tempDto.setPersonId(persons.get(0).getId());
        return tempDto;
    }

    @Test
    void add() {
        createPersons();
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
        String password = PASSWORD.substring(0, 7);
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

        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
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

        List<UserAccountDTO> all = userAccountService.getAll();
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.ALREADY_REGISTERED_PERSON, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void add_AlreadyRegisteredUsername() {
        createPersons();
        dtoWithPassword = getDtoWithPassword();
        result = userAccountService.add(dtoWithPassword);
        assertTrue(userAccountService.getAll().contains(result));
        dtoWithPassword.setPersonId(persons.get(1).getId());

        List<UserAccountDTO> all = userAccountService.getAll();
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.add(dtoWithPassword));
        assertEquals(ExceptionType.ALREADY_REGISTERED_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    private List<UserAccountDTO> createUsers() {
        return createUsers(null);
    }

    private List<UserAccountDTO> createUsers(Long num) {
        createPersons();
        List<UserAccountDTO> output = new LinkedList<>();
        List<String> rolesNames = new LinkedList<>();
        int i = 0;
        for (PersonDTO person : persons) {
            dtoWithPassword = getDtoWithPassword(num == null ? null : num + i);
            rolesNames.add(ROLES.get(i).getName());
            dtoWithPassword.setRoles(rolesNames);
            dtoWithPassword.setPersonId(person.getId());
            dtoWithPassword.setUsername(USERNAME + '_' + i);
            output.add(userAccountService.add(dtoWithPassword));
            i++;
        }
        return output;
    }

    @Test
    void delete() {
        List<UserAccountDTO> users = createUsers();
        List<UserAccountDTO> all = userAccountService.getAll();
        assertEquals(users.size(), all.size());
        assertTrue(all.containsAll(users));
        UserAccountDTO deleted;
        int index;

        int size = users.size();
        for (int i = 0; i < size; i++) {
            deleted = users.get(0);
            users.remove(0);
            result = userAccountService.delete(deleted.getId());
            all = userAccountService.getAll();
            assertEquals(deleted, result);
            assertEquals(users.size(), all.size());
            assertTrue(all.containsAll(users));
            assertFalse(all.contains(deleted));
        }
    }

    @Test
    void delete_NullId() {
        createUsers();
        Long id = null;
        all = userAccountService.getAll();
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.delete(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void delete_NotRecordedId() {
        List<UserAccountDTO> users = createUsers();
        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());
        long finalId = id;
        List<UserAccountDTO> all = userAccountService.getAll();
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.delete(finalId));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertTrue(userAccountService.getAll().containsAll(all));
    }

    @Test
    void delete_ExistingRecord() {
        users = createUsers();
        dto = users.get(0);

        NomenclatureDTO nomenclature = new NomenclatureDTO();
        nomenclature.setName("NOMENCLATURE_NAME");
        nomenclature = nomenclatureService.createNew(
                Collections.singletonList(nomenclature)).get(0);
        RecordInputDTO recordInput = new RecordInputDTO();
        recordInput.setAmount(2L);
        recordInput.setType(RecordType.RECEPTION.name());
        recordInput.setNomenclatureId(nomenclature.getId());
        recordService.add(recordInput, dto.getUsername());

        List<UserAccountDTO> all = userAccountService.getAll();
        ProhibitedRemovingException e = assertThrows(ProhibitedRemovingException.class,
                () -> userAccountService.delete(dto.getId()));
        assertEquals(ExceptionType.USER_HAS_RECORDS, e.getExceptionType());
        assertTrue(userAccountService.getAll().containsAll(all));
    }

    @Test
    void updateUsername() {
        users = createUsers();
        final int index = 1;
        dto = users.get(index);
        users.remove(index);
        String usernameBeforeUpdate = dto.getUsername();
        dto.setUsername("some" + dto.getUsername());
        Collection<String> rolesBeforeUpdate = dto.getRoles();
        dto.setRoles(Collections.singletonList(ROLES.get(2).getName()));
        long personBeforeUpdate = dto.getPersonId();
        dto.setPersonId(users.get(index + 1).getPersonId());
        dto.setIsEnabled(true);
        String passwordBeforeUpdate = userAccountService.getEntityById(dto.getId()).getPassword();

        result = userAccountService.updateUsername(dto);
        all = userAccountService.getAll();
        assertEquals(dto.getId(), result.getId());
        assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
        assertEquals(rolesBeforeUpdate, result.getRoles());
        assertEquals(personBeforeUpdate, result.getPersonId());
        assertEquals(1 + users.size(), all.size());
        assertFalse(result.getIsEnabled());
        assertEquals(passwordBeforeUpdate, userAccountService.getEntityById(result.getId()).getPassword());
        assertTrue(all.containsAll(users));
        assertTrue(all.contains(result));

        userAccountService.enable(dto.getId());

        dto.setUsername(usernameBeforeUpdate);
        dto.setIsEnabled(false);
        result = userAccountService.updateUsername(dto);
        assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
        assertTrue(result.getIsEnabled());
    }

    @Test
    void updateUsername_UserAccountWithPasswordDto() {
        if (UserAccountDTO.class.isAssignableFrom(UserAccountWithPasswordDTO.class)) {
            users = createUsers();
            final int index = 1;
            dto = users.get(index);
            users.remove(index);
            String usernameBeforeUpdate = dto.getUsername();
            dto.setUsername("some" + dto.getUsername());
            Collection<String> rolesBeforeUpdate = dto.getRoles();
            dto.setRoles(Collections.singletonList(ROLES.get(2).getName()));
            long personBeforeUpdate = dto.getPersonId();
            dto.setPersonId(users.get(index + 1).getPersonId());
            dto.setIsEnabled(true);

            String passwordBeforeUpdate = userAccountService.getEntityById(dto.getId()).getPassword();
            dtoWithPassword = convert(dto);
            dtoWithPassword.setPassword(PASSWORD + USERNAME);

            result = userAccountService.updateUsername(dtoWithPassword);
            all = userAccountService.getAll();
            dto = dtoWithPassword;
            assertEquals(dto.getId(), result.getId());
            assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
            assertEquals(rolesBeforeUpdate, result.getRoles());
            assertEquals(personBeforeUpdate, result.getPersonId());
            assertEquals(1 + users.size(), all.size());
            assertFalse(result.getIsEnabled());
            assertTrue(all.containsAll(users));
            assertTrue(all.contains(result));
            assertFalse(userEncoder.matches(dtoWithPassword.getPassword(),
                    userAccountService.getEntityById(result.getId()).getPassword()));
            assertEquals(passwordBeforeUpdate, userAccountService.getEntityById(result.getId()).getPassword());

            userAccountService.enable(dto.getId());

            dtoWithPassword.setUsername(usernameBeforeUpdate);
            dtoWithPassword.setIsEnabled(false);
            result = userAccountService.updateUsername(dtoWithPassword);
            dto = dtoWithPassword;
            assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
            assertTrue(result.getIsEnabled());
        }
    }

    @Test
    void updateUsername_NullId() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setId(null);
        dto.setUsername("some" + dto.getUsername());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateUsername(dto));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateUsername_NotRecordedId() {
        users = createUsers();
        all = userAccountService.getAll();

        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());

        dto = users.get(1);
        dto.setId(id);
        dto.setUsername("some" + dto.getUsername());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.updateUsername(dto));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateUsername_NullUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setUsername(NULL_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateUsername(dto));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateUsername_EmptyUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setUsername(EMPTY_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateUsername(dto));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateUsername_SpaceUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setUsername(SPACE_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateUsername(dto));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateUsername_BlankUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setUsername(BLANK_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateUsername(dto));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword() {
        users = createUsers();
        final int index = 1;
        dto = users.get(index);
        users.remove(index);
        Collection<String> rolesBeforeUpdate = dto.getRoles();
        dto.setRoles(Collections.singletonList(ROLES.get(2).getName()));
        long personBeforeUpdate = dto.getPersonId();
        dto.setPersonId(users.get(index + 1).getPersonId());
        dto.setIsEnabled(true);

        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(PASSWORD + USERNAME);

        result = userAccountService.updatePassword(dtoWithPassword);
        all = userAccountService.getAll();
        dto = dtoWithPassword;
        assertEquals(dto.getId(), result.getId());
        assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
        assertEquals(rolesBeforeUpdate, result.getRoles());
        assertEquals(personBeforeUpdate, result.getPersonId());
        assertFalse(result.getIsEnabled());
        assertEquals(1 + users.size(), all.size());
        assertTrue(all.containsAll(users));
        assertTrue(all.contains(result));
        assertTrue(userEncoder.matches(dtoWithPassword.getPassword(),
                userAccountService.getEntityById(result.getId()).getPassword()));

        userAccountService.enable(dto.getId());

        dtoWithPassword.setPassword(USERNAME + System.nanoTime() % 10000);
        dtoWithPassword.setIsEnabled(false);
        result = userAccountService.updatePassword(dtoWithPassword);
        assertTrue(userEncoder.matches(dtoWithPassword.getPassword(),
                userAccountService.getEntityById(result.getId()).getPassword()));
        assertTrue(result.getIsEnabled());
    }

    private UserAccountWithPasswordDTO convert(UserAccountDTO dto) {
        UserAccountWithPasswordDTO result = new UserAccountWithPasswordDTO();
        result.setId(dto.getId());
        result.setUsername(dto.getUsername());
        result.setRoles(dto.getRoles());
        result.setIsEnabled(dto.getIsEnabled());
        result.setPersonId(dto.getPersonId());
        return result;
    }

    @Test
    void updatePassword_WrongUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setUsername(dto.getUsername() + (char) ('A' + System.nanoTime() % 20));
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(System.nanoTime() % 10 + PASSWORD);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.ID_USERNAME_INCORRECT, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_NullId() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dto.setId(null);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(System.nanoTime() % 10 + PASSWORD);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_NotRecordedId() {
        users = createUsers();
        all = userAccountService.getAll();

        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());

        dto = users.get(1);
        dto.setId(id);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(System.nanoTime() % 10 + PASSWORD);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_NullPassword() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(NULL_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_EmptyPassword() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(EMPTY_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_SpacePassword() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(SPACE_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_BlankPassword() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(NULL_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updatePassword_NotValidPassword() {
        users = createUsers();
        all = userAccountService.getAll();

        dto = users.get(1);
        dtoWithPassword = convert(dto);
        dtoWithPassword.setPassword(PASSWORD.substring(0, 7));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updatePassword(dtoWithPassword));
        assertEquals(ExceptionType.NOT_VALID_PASSWORD, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles() {
        users = createUsers();
        final int index = 1;
        dto = users.get(index);
        users.remove(index);
        dto.setRoles(Collections.singletonList(ROLES.get(index + 1).getName()));
        long personBeforeUpdate = dto.getPersonId();
        dto.setPersonId(users.get(index + 1).getPersonId());
        dto.setIsEnabled(true);
        String passwordBeforeUpdate = userAccountService.getEntityById(dto.getId()).getPassword();

        result = userAccountService.updateRoles(dto);
        all = userAccountService.getAll();
        assertEquals(dto.getId(), result.getId());
        assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
        assertEquals(dto.getRoles().size(), result.getRoles().size());
        assertTrue(result.getRoles().containsAll(dto.getRoles()));
        assertEquals(personBeforeUpdate, result.getPersonId());
        assertEquals(1 + users.size(), all.size());
        assertFalse(result.getIsEnabled());
        assertTrue(all.containsAll(users));
        assertTrue(all.contains(result));
        assertEquals(passwordBeforeUpdate, userAccountService.getEntityById(result.getId()).getPassword());

        userAccountService.enable(dto.getId());

        dto.setRoles(users.get(0).getRoles());
        dto.setIsEnabled(false);
        result = userAccountService.updateRoles(dto);
        assertEquals(dto.getRoles().size(), result.getRoles().size());
        assertTrue(result.getRoles().containsAll(dto.getRoles()));
        assertTrue(result.getIsEnabled());
    }

    @Test
    void updateUserRoles_UserAccountWithPasswordDto() {
        if (UserAccountDTO.class.isAssignableFrom(UserAccountWithPasswordDTO.class)) {
            users = createUsers();
            final int index = 1;
            dto = users.get(index);
            users.remove(index);

            dto.setRoles(Collections.singletonList(ROLES.get(index + 1).getName()));
            long personBeforeUpdate = dto.getPersonId();
            dto.setPersonId(users.get(index + 1).getPersonId());
            dto.setIsEnabled(true);

            String passwordBeforeUpdate = userAccountService.getEntityById(dto.getId()).getPassword();
            dtoWithPassword = convert(dto);
            dtoWithPassword.setPassword(PASSWORD + USERNAME);

            result = userAccountService.updateRoles(dtoWithPassword);
            all = userAccountService.getAll();
            dto = dtoWithPassword;
            assertEquals(dto.getId(), result.getId());
            assertTrue(result.getUsername().equalsIgnoreCase(dto.getUsername()));
            assertEquals(dto.getRoles().size(), result.getRoles().size());
            assertTrue(result.getRoles().containsAll(dto.getRoles()));
            assertEquals(personBeforeUpdate, result.getPersonId());
            assertEquals(1 + users.size(), all.size());
            assertFalse(result.getIsEnabled());
            assertTrue(all.containsAll(users));
            assertTrue(all.contains(result));
            assertFalse(userEncoder.matches(dtoWithPassword.getPassword(),
                    userAccountService.getEntityById(result.getId()).getPassword()));
            assertEquals(passwordBeforeUpdate, userAccountService.getEntityById(result.getId()).getPassword());

            userAccountService.enable(dto.getId());

            dto.setRoles(users.get(0).getRoles());
            dto.setIsEnabled(false);
            dtoWithPassword = convert(dto);
            result = userAccountService.updateRoles(dtoWithPassword);
            assertEquals(dto.getRoles().size(), result.getRoles().size());
            assertTrue(result.getRoles().containsAll(dto.getRoles()));
            assertTrue(result.getIsEnabled());
            assertEquals(passwordBeforeUpdate, userAccountService.getEntityById(result.getId()).getPassword());
        }
    }

    @Test
    void updateRoles_WrongUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(Collections.singletonList(ROLES.get(index + 1).getName()));
        dto.setUsername(dto.getUsername() + (char) (System.nanoTime() % 20));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.ID_USERNAME_INCORRECT, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_NullId() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(Collections.singletonList(ROLES.get(index + 1).getName()));
        dto.setId(null);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_NotRecordedId() {
        users = createUsers();
        all = userAccountService.getAll();

        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(Collections.singletonList(ROLES.get(index + 1).getName()));
        dto.setId(id);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_NullRolesList() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(null);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_EmptyRolesList() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(Collections.emptyList());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_NullRoleName() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(new LinkedList<>(dto.getRoles()));
        dto.getRoles().add(NULL_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_EmptyRoleName() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(new LinkedList<>(dto.getRoles()));
        dto.getRoles().add(EMPTY_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_SpaceRoleName() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(new LinkedList<>(dto.getRoles()));
        dto.getRoles().add(SPACE_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_BlankRoleName() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(new LinkedList<>(dto.getRoles()));
        dto.getRoles().add(BLANK_STR);

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void updateRoles_NotExistingRoleName() {
        users = createUsers();
        all = userAccountService.getAll();

        final int index = 1;
        dto = users.get(index);
        dto.setRoles(new LinkedList<>(dto.getRoles()));
        dto.getRoles().add("ROLE_SOMETHING");

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.updateRoles(dto));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void enable() {
        users = createUsers();
        List<UserAccountDTO> updated = new LinkedList<>();

        while (!users.isEmpty()) {
            dto = users.get((int) System.nanoTime() % users.size());
            users.remove(dto);
            result = userAccountService.enable(dto.getId());
            dto.setIsEnabled(true);
            assertEquals(dto, result);
            updated.add(result);
            all = userAccountService.getAll();
            assertEquals(users.size() + updated.size(), all.size());
            assertTrue(all.containsAll(users));
            assertTrue(all.containsAll(updated));
        }
    }

    @Test
    void enable_NullId() {
        createUsers();
        Long id = null;
        all = userAccountService.getAll();
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.enable(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
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
    void enable_NotRecordedId() {
        users = createUsers();
        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());
        all = userAccountService.getAll();
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.enable(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void disable() {
        users = createUsers();
        List<UserAccountDTO> updated = new LinkedList<>();

        while (!users.isEmpty()) {
            dto = users.get((int) System.nanoTime() % users.size());
            users.remove(dto);
            userAccountService.enable(dto.getId());

            result = userAccountService.disable(dto.getId());
            dto.setIsEnabled(false);
            assertEquals(dto, result);
            updated.add(result);
            all = userAccountService.getAll();
            assertEquals(users.size() + updated.size(), all.size());
            assertTrue(all.containsAll(users));
            assertTrue(all.containsAll(updated));
        }
    }

    @Test
    void disable_NullId() {
        createUsers();
        Long id = null;
        all = userAccountService.getAll();
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.disable(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void disable_NotRecordedId() {
        users = createUsers();
        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());
        all = userAccountService.getAll();
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.disable(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getAll() {
        assertTrue(userAccountService.getAll().isEmpty());

        users = createUsers();
        all = userAccountService.getAll();
        assertEquals(users.size(), all.size());
        assertTrue(all.containsAll(users));

        while (!users.isEmpty()) {
            dto = userAccountService.delete(users.get((int) (System.nanoTime() % users.size())).getId());
            users.remove(dto);
            all = userAccountService.getAll();
            assertEquals(users.size(), all.size());
            assertTrue(all.containsAll(users));
            assertFalse(all.contains(dto));
        }

        assertTrue(userAccountService.getAll().isEmpty());
    }

    @Test
    void getById() {
        users = createUsers();
        all = userAccountService.getAll();

        assertFalse(users.isEmpty());
        users.forEach(user -> assertEquals(user, userAccountService.getById(user.getId())));
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getById_NullId() {
        createUsers();
        Long id = null;
        all = userAccountService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getById_NotRecordedId() {
        users = createUsers();
        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());
        all = userAccountService.getAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.getById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getEntityById() {
        final Long num = 2L;
        users = createUsers(num);
        all = userAccountService.getAll();

        assertFalse(users.isEmpty());
        for (UserAccountDTO user : users) {
            resultEntity = userAccountService.getEntityById(user.getId());
            assertEquals(user.getId(), resultEntity.getId());
            assertEquals(user.getUsername(), resultEntity.getUsername());
            assertEquals(user.getIsEnabled(), resultEntity.isEnabled());
            assertEquals(user.getPersonId(), resultEntity.getPerson().getId());
            assertEquals(user.getRoles().size(), resultEntity.getRoles().size());
            assertTrue(resultEntity.getRoles().stream().map(RoleEntity::getName).toList().containsAll(user.getRoles()));
            assertTrue(userEncoder.matches(PASSWORD + (num + users.indexOf(user)), resultEntity.getPassword()));
        }
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getEntityById_NullId() {
        createUsers();
        Long id = null;
        all = userAccountService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getEntityById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getEntityById_NotRecordedId() {
        users = createUsers();
        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());
        all = userAccountService.getAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByPersonId() {
        users = createUsers();
        all = userAccountService.getAll();

        assertFalse(users.isEmpty());
        users.forEach(user -> assertEquals(user, userAccountService.getByPersonId(user.getPersonId())));
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByPersonId_NullId() {
        createUsers();
        Long id = null;
        all = userAccountService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getByPersonId(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByPersonId_NotRecordedId() {
        users = createUsers();
        long id = getNotRecordedId(persons.stream().map(PersonDTO::getId).toList());
        all = userAccountService.getAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.getByPersonId(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByUsername() {
        users = createUsers();
        all = userAccountService.getAll();

        assertFalse(users.isEmpty());
        users.forEach(user -> assertEquals(user,
                userAccountService.getByUsername(user.getUsername().toLowerCase(Locale.ROOT))));
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByUsername_NotRecordedUsername() {
        users = createUsers();
        all = userAccountService.getAll();
        String username = users.get(0).getUsername() + (char) (System.nanoTime() % 20 + 'A');

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.getByUsername(username));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByUsername_NullUsername() {
        createUsers();
        all = userAccountService.getAll();
        String username = NULL_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByUsername_EmptyUsername() {
        createUsers();
        all = userAccountService.getAll();
        String username = EMPTY_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByUsername_SpaceUsername() {
        createUsers();
        all = userAccountService.getAll();
        String username = SPACE_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void getByUsername_BlankUsername() {
        createUsers();
        all = userAccountService.getAll();
        String username = BLANK_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.getByUsername(username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void findAllByRole() {
        users = createUsers();
        all = userAccountService.getAll();
        List<UserAccountDTO> correct;
        List<UserAccountDTO> result;

        assertFalse(users.isEmpty());
        for (RoleEntity role : ROLES) {
            correct = users.stream().filter(user -> user.getRoles().contains(role.getName())).toList();
            assertFalse(correct.isEmpty());
            correct.forEach(u -> log.info(u.toString()));
            result = userAccountService.findAllByRole(role.getName().toLowerCase(Locale.ROOT));
            assertEquals(correct.size(), result.size());
            assertTrue(result.containsAll(correct));
            result = userAccountService.findAllByRole(role.getName().toUpperCase(Locale.ROOT));
            assertEquals(correct.size(), result.size());
            assertTrue(result.containsAll(correct));
        }

        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void findAllByRole_NotRecordedRoleName() {
        users = createUsers();
        all = userAccountService.getAll();
        String roleName = "ROLE_SOMETHING";

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userAccountService.findAllByRole(roleName));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void findAllByRole_NullRoleName() {
        createUsers();
        all = userAccountService.getAll();
        String roleName = NULL_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.findAllByRole(roleName));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void findAllByRole_EmptyRoleName() {
        createUsers();
        all = userAccountService.getAll();
        String roleName = EMPTY_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.findAllByRole(roleName));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void findAllByRole_SpaceRoleName() {
        createUsers();
        all = userAccountService.getAll();
        String roleName = SPACE_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.findAllByRole(roleName));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }

    @Test
    void findAllByRole_BlankRoleName() {
        createUsers();
        all = userAccountService.getAll();
        String roleName = BLANK_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> userAccountService.findAllByRole(roleName));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, userAccountService.getAll());
    }


}
















