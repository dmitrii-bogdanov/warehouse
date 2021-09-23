package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.dto.*;
import bogdanov.warehouse.dto.search.SearchRecordDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.ElementType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
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
    @Autowired
    private NomenclatureRepository nomenclatureRepository;
    @Autowired
    private RecordTypeService recordTypeService;

    private UserAccountDTO testUser = null;
    private RecordDTO input = null;
    private RecordDTO dto = null;
    private RecordDTO result = null;
    private RecordDTO output = null;

    private List<PersonDTO> persons = new LinkedList<>();
    private List<UserAccountDTO> users = new LinkedList<>();
    private List<RecordDTO> all = new LinkedList<>();
    private List<NomenclatureDTO> nomenclature = new LinkedList<>();
    private List<RecordDTO> records = new LinkedList<>();
    private List<ReverseRecordDTO> reverseRecords = new LinkedList<>();
    private List<RecordDTO> reverseGeneratedRecords = new LinkedList<>();

    private final Random generator = new Random(System.nanoTime());
    private final int PERSON_LIST_SIZE = 3; //<=3
    private final String RECEPTION = RecordType.RECEPTION.name();
    private final String RELEASE = RecordType.RELEASE.name();
    private final String NULL_STR = null;
    private final String EMPTY_STR = Strings.EMPTY;
    private final String SPACE_STR = " ";
    private final String BLANK_STR = "\t \t\t   \t\t\t  \t      ";

    @BeforeEach
    private void clear() {
        reverseRecordRepository.deleteAll();
        reverseRecords.clear();
        recordRepository.deleteAll();
        records.clear();
        reverseGeneratedRecords.clear();

        userAccountService.getAll().forEach(u -> userAccountService.delete(u.getId()));
        users.clear();
        personService.getAll().forEach(p -> personService.delete(p.getId()));
        persons.clear();
        nomenclatureRepository.deleteAll();
        nomenclature.clear();

        all.clear();
        input = null;
        dto = null;
        result = null;
        output = null;
    }

    private void createPersons() {
        if (persons.isEmpty()) {
            String firstname = "firstname";
            String lastname = "lastname";
            LocalDate date = LocalDate.of(2001, 1, 1);
            String position = "position";
            for (int i = 0; i < PERSON_LIST_SIZE; i++) {
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
        if (users.isEmpty()) {
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
                users.add(userAccountService.add(user));
                i++;
            }
            testUser = users.get(users.size() - 1);
        }
    }

    private void createNomenclature() {
        if (nomenclature.isEmpty()) {
            createUsers();
            final String NAME = "nomenclature_name_";
            final String CODE = "code_";
            for (int i = 0; i < persons.size(); i++) {
                nomenclature.add(new NomenclatureDTO(
                        null, NAME + i, CODE + i, null));
            }
            nomenclature = new LinkedList<>(nomenclatureService.createNew(nomenclature));
            long i = 1;
            for (NomenclatureDTO n : nomenclature) {
                n.setAmount(2 * i++);
                nomenclature.set(nomenclature.indexOf(n), nomenclatureService.addAmount(n));
            }
        }
    }

    private void createRecords() {
        createNomenclature();
        final long receptionMult = 20;
        final long releaseMult = 3;
        int i = 1;
        for (NomenclatureDTO n : nomenclature) {
            input = new RecordDTO();
            input.setType(RECEPTION);
            input.setAmount(i * receptionMult);
            input.setNomenclatureId(n.getId());
            records.add(recordService.add(input, users.get(i - 1).getUsername()));
            i++;
        }
        for (NomenclatureDTO n : nomenclature) {
            input.setType(RELEASE);
            input.setAmount(n.getAmount() / 3 + i * releaseMult);
            input.setNomenclatureId(n.getId());
            records.add(recordService.add(input, users.get(6 - i).getUsername()));
            i++;
        }
        input = null;
    }

    private void createReverseRecords() {
        createRecords();
        for (int i = records.size() - 1; i >= 0; i--) {
            reverseGeneratedRecords.add(recordService.revert(records.get(i).getId(), users.get(rand(users)).getUsername()));
        }
        reverseRecords.addAll(recordService.getAllReverseRecords());
    }

    private int rand(Collection collection) {
        return rand(collection.size());
    }

    private int rand(int bound) {
        return generator.nextInt(bound);
    }

    private List<RecordDTO> formatTime(List<RecordDTO> records) {
        return records.stream().map(this::formatTime).toList();
    }

    private RecordDTO formatTime(RecordDTO dto) {
        dto.setTime(formatTime(dto.getTime()));
        return dto;
    }

    private LocalDateTime formatTime(LocalDateTime time) {
        LocalDateTime tmp = time;
        int nano = tmp.getNano();
        nano = (nano / 1000 + (nano % 1000 >= 500 ? 1 : 0)) * 1000;
        boolean plusSecond = nano / 1_000_000_000 > 0;
        nano = nano % 1_000_000_000;
        tmp = LocalDateTime.of(tmp.getYear(), tmp.getMonthValue(), tmp.getDayOfMonth(), tmp.getHour(), tmp.getMinute(), tmp.getSecond(), nano);
        if (plusSecond) {
            tmp = tmp.plusSeconds(1);
        }
        return tmp;
    }

    @Test
    void add() {
        createNomenclature();
        long amount;
        long recordAmount;
        NomenclatureDTO nomenclatureDTO = null;
        UserAccountDTO user;

        assertTrue(recordService.getAll().isEmpty());

        for (int i = 0; i < 10; i++) {
            input = new RecordDTO();
            input.setAmount(recordAmount = (rand(20) + 1));
            input.setType(RECEPTION.toLowerCase(Locale.ROOT));
            nomenclatureDTO = nomenclatureService.getById(nomenclature.get(rand(nomenclature)).getId());
            input.setNomenclatureId(nomenclatureDTO.getId());
            amount = nomenclatureDTO.getAmount();
            user = users.get(rand(users));
            output = recordService.add(input, user.getUsername().toLowerCase(Locale.ROOT));
            records.add(output);
            assertTrue(output.getId() > 0);
            assertTrue(output.getType().equalsIgnoreCase(input.getType()));
            assertEquals(input.getNomenclatureId(), output.getNomenclatureId());
            assertEquals(user.getId(), output.getUserId());
            assertEquals(recordAmount, output.getAmount());
            assertEquals(amount + recordAmount, nomenclatureService.getById(nomenclatureDTO.getId()).getAmount());
            assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
            assertTrue(output.getTime().isBefore(LocalDateTime.now()));
            all = recordService.getAll();
            assertEquals(records.size(), all.size());
            assertTrue(all.containsAll(formatTime(records)));
        }

        for (int i = 0; i < 10; i++) {
            input = new RecordDTO();
            nomenclatureDTO = nomenclatureService.getById(nomenclature.get(rand(nomenclature)).getId());
            input.setNomenclatureId(nomenclatureDTO.getId());
            amount = nomenclatureDTO.getAmount();
            if (amount == 0) {
                i--;
                continue;
            }
            input.setAmount(recordAmount = (amount > 1 ? (rand((int) amount - 1) + 1) : 1));
            input.setType(RELEASE.toLowerCase(Locale.ROOT));
            user = users.get(rand(users));
            output = recordService.add(input, user.getUsername().toLowerCase(Locale.ROOT));
            records.add(output);
            assertTrue(output.getId() > 0);
            assertTrue(output.getType().equalsIgnoreCase(input.getType()));
            assertEquals(input.getNomenclatureId(), output.getNomenclatureId());
            assertEquals(user.getId(), output.getUserId());
            assertEquals(recordAmount, output.getAmount());
            assertEquals(amount - recordAmount, nomenclatureService.getById(nomenclatureDTO.getId()).getAmount());
            assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
            assertTrue(output.getTime().isBefore(LocalDateTime.now()));
            all = recordService.getAll();
            assertEquals(records.size(), all.size());
            assertTrue(all.containsAll(formatTime(records)));
        }

        input = new RecordDTO();
        long id = records.get(0).getId();
        input.setId(id);
        nomenclatureDTO = nomenclatureService.getById(nomenclature.get(rand(nomenclature)).getId());
        input.setNomenclatureId(nomenclatureDTO.getId());
        amount = nomenclatureDTO.getAmount();
        input.setAmount(recordAmount = (rand(10) + 1));
        input.setType(RECEPTION.toUpperCase(Locale.ROOT));
        user = users.get(rand(users));
        output = recordService.add(input, user.getUsername().toLowerCase(Locale.ROOT));
        records.add(output);
        assertTrue(output.getId() > 0);
        assertNotEquals(id, output.getId());
        assertTrue(output.getType().equalsIgnoreCase(input.getType()));
        assertEquals(input.getNomenclatureId(), output.getNomenclatureId());
        assertEquals(user.getId(), output.getUserId());
        assertEquals(recordAmount, output.getAmount());
        assertEquals(amount + recordAmount, nomenclatureService.getById(nomenclatureDTO.getId()).getAmount());
        assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
        assertTrue(output.getTime().isBefore(LocalDateTime.now()));
        all = recordService.getAll();
        assertEquals(records.size(), all.size());
        assertTrue(all.containsAll(formatTime(records)));

        release:
        {
            input = new RecordDTO();
            id = records.get(0).getId();
            input.setId(id);
            nomenclatureDTO = nomenclatureService.getById(nomenclature.get(rand(nomenclature)).getId());
            amount = nomenclatureDTO.getAmount();
            if (amount == 0) {
                break release;
            }
            input.setNomenclatureId(nomenclatureDTO.getId());
            input.setAmount(recordAmount = (amount > 1 ? (rand((int) amount - 1) + 1) : 1));
            input.setType(RELEASE.toUpperCase(Locale.ROOT));
            user = users.get(rand(users));
            output = recordService.add(input, user.getUsername().toLowerCase(Locale.ROOT));
            records.add(output);
            assertTrue(output.getId() > 0);
            assertNotEquals(id, output.getId());
            assertTrue(output.getType().equalsIgnoreCase(input.getType()));
            assertEquals(input.getNomenclatureId(), output.getNomenclatureId());
            assertEquals(user.getId(), output.getUserId());
            assertEquals(recordAmount, output.getAmount());
            assertEquals(amount - recordAmount, nomenclatureService.getById(nomenclatureDTO.getId()).getAmount());
            assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
            assertTrue(output.getTime().isBefore(LocalDateTime.now()));
            all = recordService.getAll();
            assertEquals(records.size(), all.size());
            assertTrue(all.containsAll(formatTime(records)));
        }
    }

    @Test
    void add_NullDto() {
        createNomenclature();
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(null, username));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_NullNomenclatureId() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(null);
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    private long getNotRecordedId(List<Long> list) {
        long id = generator.nextLong() & 1023;
        while (list.contains(id)) {
            id = generator.nextLong() & 1023;
        }
        return id;
    }

    @Test
    void add_NotRecordedNomenclatureId() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(
                getNotRecordedId(nomenclature.stream().map(NomenclatureDTO::getId).toList()));
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_NullUsername() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, NULL_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_EmptyUsername() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, EMPTY_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_SpaceUsername() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, SPACE_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_BlankUsername() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, BLANK_STR));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_NotRegisteredUsername() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.add(input, "Something"));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_NullType() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(NULL_STR);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_EmptyType() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(EMPTY_STR);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_SpaceType() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(SPACE_STR);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_BlankType() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(BLANK_STR);
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_WrongType() {
        createNomenclature();
        input = new RecordDTO();
        input.setType("Something");
        input.setAmount(20L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReceptionNullAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(null);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReceptionNegativeAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount((long) -rand(10));
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReceptionZeroAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(0L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReleaseNullAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RELEASE);
        input.setAmount(null);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReleaseNegativeAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RELEASE);
        input.setAmount((long) -rand(10));
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReleaseZeroAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RELEASE);
        input.setAmount(0L);
        input.setNomenclatureId(nomenclature.get(0).getId());
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_POSITIVE_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_LongValueOverfow() {
        createNomenclature();
        String username = users.get(0).getUsername();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(Long.MAX_VALUE / 2 + 1);
        input.setNomenclatureId(nomenclature.get(0).getId());
        recordService.add(input, username);
        all = recordService.getAll();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.LONG_VALUE_OVERFLOW, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void add_ReleaseNotEnoughAmount() {
        createNomenclature();
        input = new RecordDTO();
        input.setType(RELEASE);
        NomenclatureDTO nomenclatureDTO = nomenclature.get(0);
        input.setNomenclatureId(nomenclatureDTO.getId());
        input.setAmount(nomenclatureDTO.getAmount() + 1);
        all = recordService.getAll();
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.add(input, username));
        assertEquals(ExceptionType.NOT_ENOUGH_AMOUNT, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert() {
        createRecords();

        reverseRecords = recordService.getAllReverseRecords();
        assertTrue(reverseRecords.isEmpty());

        List<ReverseRecordDTO> tmp;
        ReverseRecordDTO reverse;
        UserAccountDTO user;
        List<RecordDTO> tmpAll;
        long amount;
        for (int i = records.size() - 1; i >= 0; i--) {
            RecordDTO record = records.get(i);
            amount = nomenclatureService.getById(record.getNomenclatureId()).getAmount();
            user = users.get(rand(users));
            all = recordService.getAll();
            output = recordService.revert(record.getId(), user.getUsername());
            reverseGeneratedRecords.add(output);
            assertNotEquals(record.getId(), output.getId());
            assertTrue(output.getId() > 0);
            assertEquals(user.getId(), output.getUserId());
            assertNotEquals(record.getType(), output.getType());
            assertEquals(record.getAmount(), output.getAmount());
            assertEquals(amount + record.getAmount() * (RECEPTION.equalsIgnoreCase(record.getType()) ? -1 : 1),
                    nomenclatureService.getById(record.getNomenclatureId()).getAmount());
            assertEquals(record.getNomenclatureId(), output.getNomenclatureId());
            assertTrue(output.getTime().isBefore(LocalDateTime.now()));
            assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
            assertNotEquals(record.getTime(), output.getTime());
            tmpAll = recordService.getAll();
            assertEquals(all.size() + 1, tmpAll.size());
            assertTrue(tmpAll.containsAll(all));
            assertTrue(tmpAll.contains(formatTime(output)));

            tmp = new LinkedList<>(recordService.getAllReverseRecords());
            assertEquals(reverseRecords.size() + 1, tmp.size());
            assertTrue(tmp.containsAll(reverseRecords));
            tmp.removeAll(reverseRecords);
            reverse = tmp.get(0);
            assertEquals(user.getId(), reverse.getUserId());
            assertEquals(record.getId(), reverse.getRevertedRecordId());
            assertEquals(output.getId(), reverse.getGeneratedRecordId());
            assertEquals(output.getTime(), reverse.getTime());

            reverseRecords = recordService.getAllReverseRecords();
        }
    }

    @Test
    void revert_AlreadyRevertedRecord() {
        createReverseRecords();
        all = recordService.getAll();

        for (RecordDTO record : records) {
            if (RELEASE.equalsIgnoreCase(record.getType())) {
                input = record;
                break;
            }
        }
        String username = users.get(0).getUsername();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.revert(input.getId(), username));
        assertEquals(ExceptionType.ALREADY_REVERTED_RECORD, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_NullId() {
        createRecords();
        all = recordService.getAll();
        String username = users.get(0).getUsername();
        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_NotRecordedId() {
        createRecords();
        all = recordService.getAll();
        String username = users.get(0).getUsername();
        long id = getNotRecordedId(records.stream().map(RecordDTO::getId).toList());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_NotRecordedUsername() {
        createRecords();
        all = recordService.getAll();
        String username = users.get(0).getUsername() + "something";
        long id = records.get(0).getId();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_NullUsername() {
        createRecords();
        all = recordService.getAll();
        String username = NULL_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_EmptyUsername() {
        createRecords();
        all = recordService.getAll();
        String username = EMPTY_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_SpaceUsername() {
        createRecords();
        all = recordService.getAll();
        String username = SPACE_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void revert_BlankUsername() {
        createRecords();
        all = recordService.getAll();
        String username = BLANK_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.revert(id, username));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update() {
        createRecords();

        reverseRecords = recordService.getAllReverseRecords();
        assertTrue(reverseRecords.isEmpty());

        List<ReverseRecordDTO> tmp;
        ReverseRecordDTO reverse;
        UserAccountDTO user;
        List<RecordDTO> tmpAll;
        NomenclatureDTO nomenclatureDTO;
        RecordDTO generated;
        long amount;
        long recordAmount;
        records = records.stream().filter(r -> RELEASE.equalsIgnoreCase(r.getType())).toList();
        for (int i = records.size() - 1; i >= 0; i--) {
            RecordDTO record = records.get(i);
            recordAmount = nomenclatureService.getById(record.getNomenclatureId()).getAmount();
            input = new RecordDTO();
            nomenclatureDTO = nomenclatureService.getById(nomenclature.get(rand(nomenclature)).getId());
            amount = nomenclatureDTO.getAmount();
            input.setNomenclatureId(nomenclatureDTO.getId());
            input.setType(rand(2) == 1 ? RECEPTION : RELEASE);
            if (amount == 0 && RELEASE.equalsIgnoreCase(input.getType())) {
                i++;
                continue;
            }
            input.setAmount(
                    RELEASE.equalsIgnoreCase(input.getType())
                            ? (long) (amount == 1 ? 1 : (rand((int) amount - 1) + 1))
                            : (long) (rand(20) + 1)
            );

            user = users.get(rand(users));
            all = recordService.getAll();
            output = recordService.update(record.getId(), user.getUsername(), input);
            reverseGeneratedRecords.add(output);
            assertNotEquals(record.getId(), output.getId());
            assertTrue(output.getId() > 0);
            assertEquals(user.getId(), output.getUserId());
            assertTrue(input.getType().equalsIgnoreCase(output.getType()));
            assertEquals(input.getAmount(), output.getAmount());
            assertEquals(input.getNomenclatureId(), output.getNomenclatureId());
            assertTrue(output.getTime().isBefore(LocalDateTime.now()));
            assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
            assertNotEquals(record.getTime(), output.getTime());
            boolean sameNomenclature = record.getNomenclatureId().equals(input.getNomenclatureId());
            assertEquals(
                    recordAmount
                            + record.getAmount() * (RECEPTION.equals(record.getType()) ? -1 : 1)
                            + input.getAmount() * (RECEPTION.equals(input.getType()) ? 1 : -1) * (sameNomenclature ? 1 : 0),
                    nomenclatureService.getById(record.getNomenclatureId()).getAmount()
            );
            if (!sameNomenclature) {
                assertEquals(
                        amount + input.getAmount() * (RECEPTION.equalsIgnoreCase(input.getType()) ? 1 : -1),
                        nomenclatureService.getById(input.getNomenclatureId()).getAmount()
                );
            }

            tmpAll = recordService.getAll();
            assertEquals(all.size() + 2, tmpAll.size());
            assertTrue(tmpAll.containsAll(all));
            assertTrue(tmpAll.contains(formatTime(output)));
            tmpAll.removeAll(all);
            tmpAll.remove(formatTime(output));
            generated = tmpAll.get(0);
            assertTrue(generated.getId() > 0);
            assertEquals(record.getAmount(), generated.getAmount());
            assertTrue((RECEPTION.equals(record.getType()) && RELEASE.equals(generated.getType()))
                    || (RELEASE.equals(record.getType()) && RECEPTION.equals(generated.getType())));
            assertEquals(record.getNomenclatureId(), generated.getNomenclatureId());
            assertEquals(user.getId(), generated.getUserId());

            tmp = new LinkedList<>(recordService.getAllReverseRecords());
            assertEquals(reverseRecords.size() + 1, tmp.size());
            assertTrue(tmp.containsAll(reverseRecords));
            tmp.removeAll(reverseRecords);
            reverse = tmp.get(0);
            assertEquals(user.getId(), reverse.getUserId());
            assertEquals(record.getId(), reverse.getRevertedRecordId());
            assertEquals(generated.getId(), reverse.getGeneratedRecordId());
            assertEquals(generated.getTime(), reverse.getTime());

            reverseRecords = recordService.getAllReverseRecords();
        }
    }

    @Test
    void update_AlreadyRevertedRecord() {
        createReverseRecords();
        all = recordService.getAll();

        long id = records.get(records.size() - 1).getId();
        String username = users.get(0).getUsername();
        input = new RecordDTO();
        input.setType(RECEPTION);
        input.setAmount(10L);
        input.setNomenclatureId(nomenclature.get(0).getId());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.ALREADY_REVERTED_RECORD, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_NullId() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = users.get(0).getUsername();
        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_NotRecordedId() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = users.get(0).getUsername();
        long id = getNotRecordedId(records.stream().map(RecordDTO::getId).toList());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_NotRecordedUsername() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = users.get(0).getUsername() + "something";
        long id = records.get(0).getId();

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_NullUsername() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = NULL_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_EmptyUsername() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = EMPTY_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_SpaceUsername() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = SPACE_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void update_BlankUsername() {
        createRecords();
        all = recordService.getAll();
        input = records.get(0);
        String username = BLANK_STR;
        long id = records.get(0).getId();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.update(id, username, input));
        assertEquals(ExceptionType.BLANK_USERNAME, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getAll() {
        all = recordService.getAll();
        assertTrue(all.isEmpty());

        createRecords();

        all = recordService.getAll();
        assertFalse(all.isEmpty());
        assertEquals(records.size(), all.size());
        assertTrue(all.containsAll(formatTime(records)));

        recordRepository.deleteAll();
        all = recordService.getAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void getEntityById() {
        createRecords();
        all = recordService.getAll();

        RecordEntity entity;
        for (RecordDTO record : records) {
            entity = recordService.getEntityById(record.getId());
            assertEquals(record.getId(), entity.getId());
            assertTrue(record.getType().equalsIgnoreCase(entity.getType().getName()));
            assertEquals(formatTime(record.getTime()), formatTime(entity.getTime()));
            assertEquals(record.getAmount(), entity.getAmount());
            assertEquals(record.getNomenclatureId(), entity.getNomenclature().getId());
            assertEquals(record.getUserId(), entity.getUser().getId());
        }
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getEntityById_NullId() {
        all = recordService.getAll();
        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.getEntityById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getEntityById_NotRecordedId() {
        all = recordService.getAll();
        long id = getNotRecordedId(records.stream().map(RecordDTO::getId).toList());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getById() {
        createRecords();
        all = recordService.getAll();

        for (RecordDTO record : records) {
            output = recordService.getById(record.getId());
            assertEquals(formatTime(record), formatTime(output));
        }
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getById_NullId() {
        all = recordService.getAll();
        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.getById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getById_NotRecordedId() {
        all = recordService.getAll();
        long id = getNotRecordedId(records.stream().map(RecordDTO::getId).toList());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordService.getById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void getAllReverseRecords() {
        all = recordService.getAll();
        List<ReverseRecordDTO> allReverseRecords = recordService.getAllReverseRecords();
        assertTrue(allReverseRecords.isEmpty());
        assertTrue(all.isEmpty());

        createReverseRecords();
        all = recordService.getAll();
        allReverseRecords = recordService.getAllReverseRecords();
        assertEquals(all, recordService.getAll());
        assertFalse(allReverseRecords.isEmpty());
        assertEquals(reverseRecords.size(), allReverseRecords.size());
        assertTrue(allReverseRecords.containsAll(reverseRecords));
    }

    @Test
    void existsByUserId() {
        createRecords();
        for (UserAccountDTO user : users) {
            assertTrue(recordService.existsByUserId(user.getId()));
        }
        long id = getNotRecordedId(users.stream().map(UserAccountDTO::getId).toList());
        assertFalse(recordService.existsByUserId(id));
    }

    private List<RecordDTO> correct(List<RecordDTO> list, SearchRecordDTO dto) {
        List<RecordDTO> result = list;
        result = result.stream().filter(r -> Strings.isBlank(dto.getType()) || r.getType().equalsIgnoreCase(dto.getType())).toList();
        result = result.stream().filter(r -> dto.getNomenclatureId() == null || dto.getNomenclatureId().isEmpty() || dto.getNomenclatureId().contains(r.getNomenclatureId())).toList();
        result = result.stream().filter(r -> dto.getUserId() == null || dto.getUserId().isEmpty() || dto.getUserId().contains(r.getUserId())).toList();
        result = result.stream().filter(r -> dto.getFromDate() == null || r.getTime().isAfter(LocalDateTime.of(dto.getFromDate(), LocalTime.of(0, 0, 0, 0)))).toList();
        result = result.stream().filter(r -> dto.getToDate() == null || r.getTime().isBefore(LocalDateTime.of(dto.getToDate(), LocalTime.of(23, 59, 59, 999_999_000)))).toList();

        return result;
    }

    private SearchRecordDTO getRandomSearch(List<RecordDTO> records) {
        SearchRecordDTO dto = null;
        boolean ok = false;
        while (!ok) {
            dto = new SearchRecordDTO();
            switch (rand(3)) {
                case 0 -> dto.setType(null);
                case 1 -> dto.setType(RECEPTION);
                case 2 -> dto.setType(RELEASE);
            }
            List<Long> list = new LinkedList<>(nomenclature.stream().map(NomenclatureDTO::getId).toList());
            List<Long> id = new LinkedList<>();
            Long n;
            int r;
            if (rand(2) == 1) {
                r = rand(list.size());
                for (int i = 0; i <= r; i++) {
                    n = list.get(rand(list.size()));
                    list.remove(n);
                    id.add(n);
                }
            }
            dto.setNomenclatureId(new LinkedList<>(id));

            id.clear();
            list = new LinkedList<>(users.stream().map(UserAccountDTO::getId).toList());
            if (rand(2) == 1) {
                r = rand(list.size());
                for (int i = 0; i <= r; i++) {
                    n = list.get(rand(list.size()));
                    list.remove(n);
                    id.add(n);
                }
            }
            dto.setUserId(new LinkedList<>(id));

            while (true) {
                dto.setFromDate(rand(2) == 0 ? null : LocalDate.now().minusDays(7).plusDays(rand(9)));
                dto.setToDate(rand(2) == 0 ? null : LocalDate.now().minusDays(7).plusDays(rand(9)));
                if (dto.getFromDate() == null || dto.getToDate() == null) {
                    break;
                } else if (dto.getFromDate().compareTo(dto.getToDate()) <= 0) {
                    break;
                }
            }

            if (!(dto.getType() == null
                    && dto.getNomenclatureId().isEmpty()
                    && dto.getUserId().isEmpty()
                    && dto.getFromDate() == null
                    && dto.getToDate() == null)) {
                ok = true;
            }
        }

        return dto;
    }

    private void changeRecordDates() {
        createRecords();
        int i = 1;
        for (RecordDTO record : records) {
            record.setTime(formatTime(record.getTime().minusDays(i++)));

            RecordEntity entity = new RecordEntity();
            entity.setId(record.getId());
            entity.setAmount(record.getAmount());
            entity.setTime(record.getTime());
            entity.setUser(userAccountService.getEntityById(record.getUserId()));
            entity.setNomenclature(nomenclatureService.getEntityById(record.getNomenclatureId()));
            entity.setType(recordTypeService.getEntityByName(record.getType()));

            recordRepository.save(entity);
        }
    }

    @Test
    void search() {
        List<RecordDTO> correct;
        List<RecordDTO> result;
        SearchRecordDTO dto;

        changeRecordDates();

        for (int i = 0; i < 100; i++) {
            all = recordService.getAll();
            dto = getRandomSearch(records);
            correct = correct(records, dto);
            result = recordService.search(dto);
            assertEquals(correct.size(), result.size());
            assertTrue(result.containsAll(correct));
            assertEquals(all, recordService.getAll());
        }
    }

    @Test
    void search_NullDto() {
        all = recordService.getAll();

        SearchRecordDTO dto = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordService.search(dto));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_NullType() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(NULL_STR);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        dto.setUserId(users.stream().map(UserAccountDTO::getId).toList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_EmptyType() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(EMPTY_STR);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        dto.setUserId(users.stream().map(UserAccountDTO::getId).toList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_SpaceType() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(SPACE_STR);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        dto.setUserId(users.stream().map(UserAccountDTO::getId).toList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_BlankType() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(BLANK_STR);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        dto.setUserId(users.stream().map(UserAccountDTO::getId).toList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_NullNomenclature() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(null);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(null);
        dto.setUserId(users.stream().map(UserAccountDTO::getId).toList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_EmptyNomenclature() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(null);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(Collections.emptyList());
        dto.setUserId(users.stream().map(UserAccountDTO::getId).toList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_NullUsers() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(null);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        dto.setUserId(null);

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }

    @Test
    void search_EmptyUsers() {
        changeRecordDates();
        all = recordService.getAll();

        SearchRecordDTO dto = new SearchRecordDTO();
        dto.setType(null);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNomenclatureId(nomenclature.stream().map(NomenclatureDTO::getId).toList());
        dto.setUserId(Collections.emptyList());

        List<RecordDTO> correct = correct(records, dto);
        List<RecordDTO> result = recordService.search(dto);

        assertEquals(correct.size(), result.size());
        assertTrue(result.containsAll(correct));
        assertEquals(all, recordService.getAll());
    }


}















