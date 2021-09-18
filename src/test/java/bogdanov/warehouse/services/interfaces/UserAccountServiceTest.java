package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

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
    private @Qualifier("user") BCryptPasswordEncoder userEncoder;
    @Autowired
    private @Qualifier("admin") BCryptPasswordEncoder adminEncoder;


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

    private final List<RoleEntity> ROLES = roleService.getAllEntities();
    private final int PERSONS_LIST_SIZE = ROLES.size();

    @BeforeEach
    private void clear() {
        recordRepository.deleteAll();
        nomenclatureService.getAll().forEach(n -> nomenclatureService.delete(n.getId()));
        userAccountService.getAll().forEach(a -> userAccountService.delete(a.getId()));
        personService.getAll().forEach(p -> personService.delete(p.getId()));

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

        input.forEach(account -> output.add(userAccountService.add(account)));

        assertEquals(input.size(), output.size());
        assertEquals(output.size(), userAccountService.getAll().size());
        assertTrue(userAccountService.getAll().containsAll(output));

        assertTrue();

        for (i = 0; i < input.size(); i++) {

            assertNotNull(output.get(i));
            assertNotNull();

        }

    }

}
















