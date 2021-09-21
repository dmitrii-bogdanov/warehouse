package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.dto.*;
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
        List<RecordDTO> recordsToBeReverted = new LinkedList<>(records);
        int i = 0;
        while (!recordsToBeReverted.isEmpty()) {
            dto = recordsToBeReverted.get((int) (System.nanoTime() % recordsToBeReverted.size()));
            recordsToBeReverted.remove(dto);
            reverseGeneratedRecords.add(recordService.revert(dto.getId(), users.get(i++).getUsername()));
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
        LocalDateTime tmp = dto.getTime();
        int nano = tmp.getNano();
        nano = (nano / 1000 + (nano % 1000 >= 500 ? 1 : 0)) * 1000;
        boolean plusSecond = nano / 1_000_000_000 > 0;
        nano = nano % 1_000_000_000;
        tmp = LocalDateTime.of(tmp.getYear(), tmp.getMonthValue(), tmp.getDayOfMonth(), tmp.getHour(), tmp.getMinute(), tmp.getSecond(), nano);
        if (plusSecond) {
            tmp = tmp.plusSeconds(1);
        }
        dto.setTime(tmp);
        return dto;
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

        release: {
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
        for (RecordDTO record : records) {
            user = users.get(rand(users));
            output = recordService.revert(record.getId(), user.getUsername());
            reverseGeneratedRecords.add(output);
            assertNotEquals(record.getId(), output.getId());
            assertTrue(output.getId() > 0);
            assertEquals(user.getId(), output.getUserId());
            assertNotEquals(record.getType(), output.getType());
            assertEquals(record.getAmount(), output.getAmount());
            assertEquals(record.getNomenclatureId(), output.getNomenclatureId());
            assertTrue(output.getTime().isBefore(LocalDateTime.now()));
            assertTrue(output.getTime().isAfter(LocalDateTime.now().minusMinutes(1)));
            assertNotEquals(record.getTime(), output.getTime());

            tmp = recordService.getAllReverseRecords();
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

}
