package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.dto.*;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class RecordServiceTest {

    @Autowired
    private RecordService recordService;
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private ReverseRecordRepository reverseRecordRepository;
    @Autowired
    private PersonService personService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private NomenclatureService nomenclatureService;

    private UserAccountDTO testUser = null;
    private RecordInputDTO input = null;
    private RecordDTO dto = null;
    private RecordDTO result = null;

    private List<PersonDTO> persons = new LinkedList<>();
    private List<UserAccountDTO> accounts = new LinkedList<>();
    private List<RecordDTO> all = new LinkedList<>();
    private List<NomenclatureDTO> nomenclature = new LinkedList<>();

    private final int PERSON_LIST_SIZE = 3; //>=3
    private final String RECEPTION = RecordType.RECEPTION.name();
    private final String RELEASE = RecordType.RELEASE.name();
    private final String NULL_STR = null;
    private final String EMPTY_STR = Strings.EMPTY;
    private final String SPACE_STR = " ";
    private final String BLANK_STR = "\t \t\t   \t\t\t  \t      ";
    
    @BeforeEach
    private void clear() {
        reverseRecordRepository.deleteAll();
        recordRepository.deleteAll();
        nomenclature.forEach(n -> nomenclatureService.subtractAmount(n));
        accounts.stream().filter(account -> !account.equals(testUser))
                .map(UserAccountDTO::getId)
                .forEach(userAccountService::delete);
        accounts.clear();
        all.clear();
    }

    private void createPersons() {
        if (persons.isEmpty()) {
            String firstname = "firstname";
            String lastname = "lastname";
            LocalDate date = LocalDate.of(2001, 1, 1);
            String position = "position";
            for (int i = 0; i < 3; i++) {
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
            roles.add(roleList.get(i));
            user.setRoles(roles);
            accounts.add(userAccountService.add(user));
            i++;
        }
        testUser = accounts.get(2);
    }

    private void createNomenclature() {
        if (nomenclature.isEmpty()) {
            createPersons();
            final String NAME = "nomenclature_name_";
            final String CODE = "code_";
            for (int i = 0; i < persons.size(); i++) {
                nomenclature.add(new NomenclatureDTO(
                        null, NAME + i, CODE + i, null));
            }
            nomenclature = new LinkedList<>(nomenclatureService.createNew(nomenclature));
        }
    }

}
