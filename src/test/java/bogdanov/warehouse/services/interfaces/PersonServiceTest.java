package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
            str =  str.toUpperCase(Locale.ROOT);
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

    private PersonDTO newDto(Long id, String firstname, String lastname, String patronymic, LocalDate date, String phoneNumber, String email, String position){
        new PersonDTO(id, firstname, lastname, patronymic, date, phoneNumber, email, position);
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

    @Test
    void add() {
        init();
        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, "firstnameB", "lastnameB", "patronymicB", LocalDate.of(1992,2,2), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position2"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", "patronymicC", LocalDate.of(1993,3,3), "+6 (833) 87 788-33", NULL_STR, "position3"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", "patronymicC", LocalDate.of(1993,3,3), NULL_STR, NULL_STR, "position3"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", NULL_STR, LocalDate.of(1993,3,3), NULL_STR, NULL_STR, "position3"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", EMPTY_STR, LocalDate.of(1993,3,3), EMPTY_STR, EMPTY_STR, "position 3 dd"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", SPACE_STR, LocalDate.of(1993,3,3), SPACE_STR, SPACE_STR, "position_sddd"));
        dto.add(newDto(1L, "firstnameC", "lastnameC", BLANK_STR, LocalDate.of(1993,3,3), BLANK_STR, BLANK_STR, "position-sadd, dd"));

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
    void add_ReservedPatronymic() {
        init();
        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, "firstnameA", "lastnameA", RESERVED_NULL_PATRONYMIC, LocalDate.of(1992,2,2), null, null, "position2"));

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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = "position";

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = NULL_STR;

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
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
        final LocalDate DATE = LocalDate.of(2000, 1,1);
        final String POSITION = NULL_STR;

        dto.add(newDto(1L, "firstnameA", "lastnameA", "patronymicA", LocalDate.of(1991,1,1), "+6 (833) 87 788-33", "ddda2_4.3@452fff_df.gkg", "position1"));
        dto.add(newDto(1L, FIRSTNAME, LASTNAME, null, DATE, null, null, POSITION));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> personService.add(dto));
        assertEquals(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS, e.getExceptionType());
        assertTrue(personService.getAll().isEmpty());
    }



}















