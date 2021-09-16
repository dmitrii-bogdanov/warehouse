package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class PersonServiceTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private UserAccountService accountService;
    @Autowired
    private PositionService positionService;
    @Autowired
    private ObjectMapper objectMapper;

    private final String NULL_STR = null;
    private final String EMPTY_STR = Strings.EMPTY;
    private final String SPACE_STR = " ";
    private final String BLANK_STR = "\t  \t\t      \t\t\t\t \t   ";
    private final String RESERVED_NULL_PATRONYMIC = "nULl";

    private List<PersonDTO> dto;
    private List<PersonDTO> result;

    @BeforeEach
    void clear() {
        accountService.getAll().forEach(a -> accountService.delete(a.getId()));
        personService.getAll().forEach(p -> personService.delete(p.getId()));
        positionService.getAll().forEach(p -> positionService.delete(p.getId()));

        dto = null;
        result = null;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (Strings.isBlank(phoneNumber)) {
            return null;
        }
        Matcher matcher = Pattern.compile("\\d").matcher(phoneNumber);
        if (!matcher.find()) {
            return null;
        }
        int plusIndex;
        boolean isStartingWithPlus = ((plusIndex = phoneNumber.indexOf('+')) > -1)
                && (plusIndex < matcher.start());
        phoneNumber = phoneNumber.replaceAll("[\\D]+", Strings.EMPTY);
        return isStartingWithPlus ? ('+' + phoneNumber) : phoneNumber;
    }

    private String toUpperCase(String str) {
        if (Strings.isNotBlank(str)) {
            str = str.toUpperCase(Locale.ROOT);
        } else {
            str = null;
        }
        return str;
    }

    private PersonDTO format(PersonDTO dto) {
        return new PersonDTO(
                dto.getId(),
                toUpperCase(dto.getFirstname()),
                toUpperCase(dto.getLastname()),
                toUpperCase(dto.getPatronymic()),
                dto.getBirth(),
                formatPhoneNumber(dto.getPhoneNumber()),
                toUpperCase(dto.getEmail()),
                toUpperCase(dto.getPosition())
        );
    }

    private List<PersonDTO> format(List<PersonDTO> dto) {
        return dto.stream().map(this::format).toList();
    }

    private PersonDTO newDto(Long id, String firstname, String lastname, String patronymic, LocalDate date, String phoneNumber, String email, String position) {
        PersonDTO p = new PersonDTO();
        p.setId(id);
        p.setFirstname(firstname);
        p.setLastname(lastname);
        p.setPatronymic(patronymic);
        p.setBirth(date);
        p.setPhoneNumber(phoneNumber);
        p.setEmail(email);
        p.setPosition(position);
        return p;
    }

    private void init() {
        dto = new LinkedList<>();
    }

    private List<PersonDTO> create() {
        List<PersonDTO> dto = new LinkedList<>();
        dto.add(newDto(null, "firstnameA", "lastnameA", null, LocalDate.of(1991, 1, 1), null, null, "positionA"));
        dto.add(newDto(null, "firstnameB", "lastnameB", null, LocalDate.of(1992, 2, 2), null, null, "positionB"));
        dto.add(newDto(null, "firstnameC", "lastnameC", null, LocalDate.of(1993, 3, 3), null, null, "positionC"));
        return new LinkedList<>(personService.add(dto));
    }

    private PersonDTO convert(PersonEntity entity) {
        return newDto(
                entity.getId(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPatronymic(),
                entity.getBirth(),
                entity.getPhoneNumber(),
                entity.getEmail(),
                entity.getPosition().getName()
        );
    }

    private long getNotRecordedId(List<PersonDTO> dto) {
        Random generator = new Random(System.nanoTime());
        long id = generator.nextLong();
        while (dto.stream().map(PersonDTO::getId).toList().contains(id)) {
            id = generator.nextLong();
        }
        return id;
    }

    private char getChar(int i) {
        return (char) (i + 'A');
    }

    private List<PersonDTO> createForSearch() {
        final String FIRSTNAME = "firstname";
        final String NAME = "name";
        final String LASTNAME = "lastname";
        final String PATRONYMIC = "patronymic";
        final LocalDate DATE = LocalDate.of(1980, 1, 1);
        final String PHONE_812 = "812";
        final String PHONE_800 = "800";
        final String DOT_COM = ".com";
        final String RU = "ru";
        final String DOMAIN = "domain";
        final String POSITION = "position";

        List<PersonDTO> list = new LinkedList<>();

        PersonDTO p;
        int i = 0;
        for (; i < 3; i++) {
            p = new PersonDTO();
            p.setFirstname(FIRSTNAME + getChar(i));
            p.setLastname(LASTNAME + getChar(i));
            p.setPatronymic(PATRONYMIC + getChar(i));
            p.setBirth(DATE.plusYears(i).plusMonths(i).plusDays(i));
            p.setPhoneNumber("+" + i + PHONE_800 + (int) getChar(i));
            p.setEmail(p.getFirstname() + "." + p.getLastname() + "@" + DOMAIN + "." + RU);
            p.setPosition(POSITION + getChar(0));
            list.add(p);
            p = null;
        }
        for (; i < 5; i++) {
            p = new PersonDTO();
            p.setFirstname(FIRSTNAME + getChar(i));
            p.setLastname(LASTNAME + getChar(i));
            p.setPatronymic(null);
            p.setBirth(DATE.plusYears(i).plusMonths(i).plusDays(i));
            p.setPhoneNumber("+" + i + PHONE_800 + (int) getChar(i));
            p.setEmail(p.getFirstname() + "." + p.getLastname() + "@" + DOMAIN + "." + RU);
            p.setPosition(POSITION + "_" + getChar(0));
            list.add(p);
            p = null;
        }
        for (; i < 8; i++) {
            p = new PersonDTO();
            p.setFirstname(NAME + getChar(i));
            p.setLastname(LASTNAME + getChar(i));
            p.setPatronymic(PATRONYMIC + getChar(i));
            p.setBirth(DATE.plusYears(i).plusMonths(i).plusDays(i));
            p.setPhoneNumber(i + PHONE_812 + (int) getChar(i));
            p.setEmail(p.getFirstname() + "." + p.getLastname() + "@" + NAME + DOT_COM);
            p.setPosition(NAME + "_" + getChar(0));
            list.add(p);
            p = null;
        }
        for (; i < 11; i++) {
            p = new PersonDTO();
            p.setFirstname(FIRSTNAME + getChar(i));
            p.setLastname(NAME + getChar(i));
            p.setPatronymic(PATRONYMIC + getChar(i));
            p.setBirth(DATE.plusYears(i).plusMonths(i).plusDays(i));
            p.setPhoneNumber(i + PHONE_812 + (int) getChar(i));
            p.setEmail(p.getFirstname() + "." + p.getLastname() + "@" + NAME + DOT_COM);
            p.setPosition(NAME + "_" + getChar(0));
            list.add(p);
            p = null;
        }
        for (; i < 14; i++) {
            p = new PersonDTO();
            p.setFirstname(FIRSTNAME + getChar(i));
            p.setLastname(NAME + getChar(i));
            p.setPatronymic(null);
            p.setBirth(DATE.plusYears(i).plusMonths(i).plusDays(i));
            p.setPhoneNumber(i + PHONE_812 + (int) getChar(i));
            p.setEmail(p.getFirstname() + "." + p.getLastname() + "@" + NAME + DOT_COM);
            p.setPosition(NAME + "_" + getChar(0));
            list.add(p);
            p = null;
        }
        for (; i < 17; i++) {
            p = new PersonDTO();
            p.setFirstname(NAME + getChar(i));
            p.setLastname(LASTNAME + getChar(i));
            p.setPatronymic(null);
            p.setBirth(DATE.plusYears(i).plusMonths(i).plusDays(i));
            p.setPhoneNumber(null);
            p.setEmail(p.getFirstname() + "." + p.getLastname() + "@" + NAME + "." + RU);
            p.setPosition(POSITION + getChar(i - 14));
            list.add(p);
            p = null;
        }
        for (; i < 20; i++) {
            p = new PersonDTO();
            p.setFirstname(NAME + getChar(i));
            p.setLastname(LASTNAME + getChar(i));
            p.setPatronymic(null);
            p.setBirth(DATE.plusYears(i - 14).plusMonths(i - 14).plusDays(i - 14));
            p.setPhoneNumber(i + PHONE_800 + PHONE_812 + (int) getChar(i + 3));
            p.setEmail(null);
            p.setPosition(POSITION + getChar(i - 17));
            list.add(p);
            p = null;
        }
        for (; i < 22; i++) {
            p = new PersonDTO();
            p.setFirstname(FIRSTNAME + getChar(i));
            p.setLastname(LASTNAME + getChar(i));
            p.setPatronymic(null);
            p.setBirth(DATE.plusYears(i - 20).plusMonths(i - 17).plusDays(i - 17));
            p.setPhoneNumber(null);
            p.setEmail(null);
            p.setPosition(POSITION + getChar(i - 17));
            list.add(p);
            p = null;
        }

        return new LinkedList<>(personService.add(list));
    }

    private List<PositionEntity> getPositionEntitiesFromString(String positions) {
        final String SEARCH_POSITION_DELIMITER = ",";
        return Arrays.stream(positions.split(SEARCH_POSITION_DELIMITER))
                .filter(Strings::isNotBlank)
                .map(Long::parseLong)
                .map(positionService::getEntityById)
                .toList();
    }

    private List<PersonDTO> correct(List<PersonDTO> list,
                                    String firstname, String lastname, String patronymic,
                                    String positionString, String phoneNumber, String email,
                                    LocalDate fromDate, LocalDate toDate) {
        List<String> positions = new LinkedList<>();
        if (Strings.isNotBlank(positionString)) {
            positions.addAll(
                    getPositionEntitiesFromString(positionString)
                            .stream().map(PositionEntity::getName).map(StringUtils::toRootUpperCase).toList());
        }
        return list.stream()
                .filter(p -> Strings.isBlank(firstname) || p.getFirstname().contains(firstname.toUpperCase(Locale.ROOT)))
                .filter(p -> Strings.isBlank(lastname) || p.getLastname().contains(lastname.toUpperCase(Locale.ROOT)))
                .filter(p -> Strings.isBlank(patronymic) || (Strings.isNotBlank(p.getPatronymic()) && p.getPatronymic().contains(patronymic.toUpperCase(Locale.ROOT))))
                .filter(p -> positions.isEmpty() || positions.contains(p.getPosition().toUpperCase(Locale.ROOT)))
                .filter(p -> Strings.isBlank(phoneNumber) || (Strings.isNotBlank(p.getEmail()) && p.getEmail().contains(email.toUpperCase(Locale.ROOT))))
                .filter(p -> Strings.isBlank(email) || (Strings.isNotBlank(p.getEmail()) && p.getEmail().contains(email.toUpperCase(Locale.ROOT))))
                .filter(p -> fromDate == null || p.getBirth().compareTo(fromDate) >= 0)
                .filter(p -> toDate == null || p.getBirth().compareTo(fromDate) <= 0)
                .toList();

    }

    @Test
    void add() {
        init();
        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, "firstnameB", "lastnameB", "patronymicB", LocalDate.of(1992, 2, 2), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position2"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", "patronymicC", LocalDate.of(1993, 3, 3), "+6 (833) 87 788-33", NULL_STR, "position3"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", "patronymicC", LocalDate.of(1993, 3, 3), NULL_STR, NULL_STR, "position3"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", NULL_STR, LocalDate.of(1993, 3, 3), NULL_STR, NULL_STR, "position3"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", EMPTY_STR, LocalDate.of(1993, 3, 3), EMPTY_STR, EMPTY_STR, "position 3 dd"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", SPACE_STR, LocalDate.of(1993, 3, 3), SPACE_STR, SPACE_STR, "position_sddd"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", BLANK_STR, LocalDate.of(1993, 3, 3), BLANK_STR, BLANK_STR, "position-sadd, dd"));

        result = personService.add(dto);

        dto = format(dto);

        assertNotNull(result);
        assertEquals(dto.size(), result.size());
        for (int i = 0; i < dto.size(); i++) {
            dto.get(i).setId(result.get(i).getId());
        }
        assertEquals(dto, result);
        assertFalse(personService.getAll().isEmpty());
        assertEquals(result, personService.getAll());

        dto = result.stream().peek(p -> p.setPatronymic(null)).toList();
        List<PersonDTO> result2 = personService.add(dto);
        assertEquals(dto.size(), result2.size());
        for (int i = 0; i < dto.size(); i++) {
            dto.get(i).setId(result.get(i).getId());
        }
        assertEquals(dto, result2);
        result.addAll(result2);
        assertEquals(result, personService.getAll());
    }

    @Test
    void add_EmptyList() {
        init();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_NullList() {
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(null));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_ReservedPatronymic() {
        init();
        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, "firstnameA", "lastnameA", RESERVED_NULL_PATRONYMIC, LocalDate.of(1992, 2, 2), null, null, "position2"));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.RESERVED_VALUE, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_NullFirstname() {
        init();
        final String FIRSTNAME = NULL_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_EmptyFirstname() {
        init();
        final String FIRSTNAME = EMPTY_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_SpaceFirstname() {
        init();
        final String FIRSTNAME = SPACE_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_BlankFirstname() {
        init();
        final String FIRSTNAME = BLANK_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_NullLastname() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = NULL_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_EmptyLastname() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = EMPTY_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_SpaceLastname() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = SPACE_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_BlankLastname() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = BLANK_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_NullDate() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = null;
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_NullPosition() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = NULL_STR;

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_EmptyPosition() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = EMPTY_STR;

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_SpacePosition() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = SPACE_STR;

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void add_BlankPosition() {
        init();
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = BLANK_STR;

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void getAll() {
        assertTrue(personService.getAll().isEmpty());
        dto = create();
        assertEquals(dto, personService.getAll());
        personService.delete(dto.get(0).getId());
        dto.remove(dto.get(0));
        assertEquals(dto, personService.getAll());
        personService.delete(dto.get(0).getId());
        dto.remove(dto.get(0));
        assertEquals(dto, personService.getAll());
        dto.addAll(personService.add(Collections.singletonList(newDto(null, "fn", "ln", null, LocalDate.of(1980, 6, 7), null, null, "pos"))));
        assertEquals(dto, personService.getAll());
        personService.delete(dto.get(0).getId());
        dto.remove(dto.get(0));
        assertEquals(dto, personService.getAll());
        personService.delete(dto.get(0).getId());
        dto.remove(dto.get(0));
        assertTrue(personService.getAll().isEmpty());
        dto.addAll(personService.add(Collections.singletonList(newDto(null, "fn", "ln", null, LocalDate.of(1980, 6, 7), null, null, "pos"))));
        assertEquals(dto, personService.getAll());
        personService.delete(dto.get(0).getId());
        dto.remove(dto.get(0));
        assertTrue(personService.getAll().isEmpty());
    }

    @Test
    void update() {
        dto = create();
        PersonDTO notUpdated = dto.get(2);
        dto.remove(2);
        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), "firstnameF", "lastnameD", "patronymicC", LocalDate.of(1992, 2, 2), "+6 (833) 87 8788-363", "ddgggda2_4.3@452cfffff_df.gkg", "positionBsaa"));

        result = personService.update(dto);

        dto = format(dto);

        assertNotNull(result);
        assertEquals(dto.size(), result.size());
        for (int i = 0; i < dto.size(); i++) {
            dto.get(i).setId(result.get(i).getId());
        }
        assertEquals(dto, result);
        assertFalse(personService.getAll().isEmpty());
        dto.add(notUpdated);
        dto = format(dto);
        assertEquals(dto, personService.getAll());
    }

    @Test
    void update_EmptyList() {
        dto = create();

        List<PersonDTO> emptyList = new LinkedList<>();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(emptyList));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void update_NullList() {
        dto = create();

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(null));
        assertEquals(ExceptionType.NO_OBJECT_WAS_PASSED, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void update_ReservedPatronymic() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        dto.remove(2);
        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), "firstnameA", "lastnameA", RESERVED_NULL_PATRONYMIC, LocalDate.of(1992, 2, 2), null, null, "position2"));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.RESERVED_VALUE, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_NullFirstname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = NULL_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_EmptyFirstname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = EMPTY_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_SpaceFirstname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = SPACE_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_BlankFirstname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = BLANK_STR;
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_NullLastname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = NULL_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_EmptyLastname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = EMPTY_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_SpaceLastname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = SPACE_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_BlankLastname() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = BLANK_STR;
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_NullDate() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = null;
        final String POSITION = "position";

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_NullPosition() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = NULL_STR;

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_EmptyPosition() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = EMPTY_STR;

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_SpacePosition() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = SPACE_STR;

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void update_BlankPosition() {
        dto = create();
        List<PersonDTO> backup = new LinkedList<>(dto);
        final String FIRSTNAME = "firstname";
        final String LASTNAME = "lastname";
        final LocalDate DATE = LocalDate.of(2000, 1, 1);
        final String POSITION = BLANK_STR;

        dto.set(0, newDto(dto.get(0).getId(), "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991, 1, 1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.set(1, newDto(dto.get(1).getId(), FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.update(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertEquals(backup, personService.getAll());
    }

    @Test
    void getEntityById() {
        dto = create();

        for (PersonDTO p : dto) {
            assertEquals(p, convert(personService.getEntityById(p.getId())));
        }
        assertEquals(dto, personService.getAll());
    }

    @Test
    void getEntityById_NotRecordedId() {
        dto = create();

        long id = getNotRecordedId(dto);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> personService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void getEntityById_NullId() {
        dto = create();

        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.getEntityById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void getById() {
        dto = create();

        for (PersonDTO p : dto) {
            assertEquals(p, personService.getById(p.getId()));
        }
        assertEquals(dto, personService.getAll());
    }

    @Test
    void getById_NotRecordedId() {
        dto = create();

        long id = getNotRecordedId(dto);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> personService.getById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void getById_NullId() {
        dto = create();

        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.getById(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionId() {
        dto = create();
        PersonDTO update = dto.get(1);
        update.setPosition(dto.get(0).getPosition());
        dto.set(1, personService.update(Collections.singletonList(update)).get(0));

        List<PersonDTO> result;
        List<PersonDTO> correct;

        for (PersonDTO p : dto) {
            PositionEntity position = positionService.getEntityByName(p.getPosition());
            result = personService.findAllByPosition(position.getId());
            correct = dto.stream().filter(person -> person.getPosition().equalsIgnoreCase(position.getName())).toList();
            assertEquals(correct.size(), result.size());
            assertTrue(result.containsAll(correct));
            result = null;
            correct = null;
            assertEquals(dto, personService.getAll());
        }
    }

    @Test
    void findAllByPositionId_NotRecordedId() {
        dto = create();
        long id = getNotRecordedId(dto);

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> personService.findAllByPosition(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionId_NullId() {
        dto = create();
        Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.findAllByPosition(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionName() {
        dto = create();
        PersonDTO update = dto.get(1);
        update.setPosition(dto.get(0).getPosition());
        dto.set(1, personService.update(Collections.singletonList(update)).get(0));

        List<PersonDTO> result;
        List<PersonDTO> correct;

        for (PersonDTO p : dto) {
            String position = p.getPosition();
            result = personService.findAllByPosition(position);
            correct = dto.stream().filter(person -> person.getPosition().equalsIgnoreCase(position)).toList();
            assertEquals(correct.size(), result.size());
            assertTrue(result.containsAll(correct));
            result = null;
            correct = null;
            assertEquals(dto, personService.getAll());
        }
    }

    @Test
    void findAllByPositionName_NotRecordedName() {
        dto = create();
        String position = dto.get(0).getPosition() + " Something";
        assertTrue(dto.stream()
                .map(PersonDTO::getPosition)
                .filter(p -> p.equalsIgnoreCase(position))
                .toList()
                .isEmpty()
        );

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> personService.findAllByPosition(position));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionName_NullName() {
        dto = create();
        String position = NULL_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.findAllByPosition(position));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionName_EmptyName() {
        dto = create();
        String position = EMPTY_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.findAllByPosition(position));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionName_SpaceName() {
        dto = create();
        String position = SPACE_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.findAllByPosition(position));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void findAllByPositionName_BlankName() {
        dto = create();
        String position = BLANK_STR;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.findAllByPosition(position));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void search_NoParameterIsPassed() {
        dto = create();
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.search(null, null, null, null, null, null, null, null));
        assertEquals(ExceptionType.NO_PARAMETER_IS_PRESENT, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void search_FromDateIsGreaterThanToDate() {
        dto = create();
        LocalDate fromDate = LocalDate.now();
        LocalDate toDate = fromDate.minusYears(2);
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.search(null, null, null, null, null, null, fromDate, toDate));
        assertEquals(ExceptionType.INCORRECT_RANGE, e.getExceptionType());
        assertEquals(dto, personService.getAll());
    }

    @Test
    void search_NotRecordedPositionId() {

    }

}















