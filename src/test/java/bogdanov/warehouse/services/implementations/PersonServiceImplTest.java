package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.exceptions.NotAllRequiredFieldsPresentException;
import bogdanov.warehouse.services.interfaces.PersonService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


//TODO Position is also required

@SpringBootTest
@AutoConfigureTestDatabase
class PersonServiceImplTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    private void clear() {
        personRepository.deleteAll();
    }

    private PersonDTO dto, result;
    private PersonEntity entity;
    private final String FIRSTNAME = "firstname".toUpperCase(Locale.ROOT);
    private final String LASTNAME = "lastname".toUpperCase(Locale.ROOT);
    private final String PATRONYMIC = "patronymic".toUpperCase(Locale.ROOT);
    private final String POSITION = "position".toUpperCase(Locale.ROOT);
    private final LocalDate DATE = LocalDate.now();
    private final String PHONE_NUMBER = "+71234567890";
    private final String EMAIL = "EMAIL";

    @BeforeEach
    private void initializeVariables() {
        dto = new PersonDTO();
        result = null;
        entity = new PersonEntity();
    }

    @Test
    void addDto_CorrectData() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setPatronymic(PATRONYMIC);
        dto.setPosition(POSITION);
        dto.setBirth(DATE);
        dto.setPhoneNumber(PHONE_NUMBER);
        dto.setEmail(EMAIL);

        result = personService.add(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
        assertEquals(FIRSTNAME, result.getFirstname());
        assertEquals(LASTNAME, result.getLastname());
        assertEquals(PATRONYMIC, result.getPatronymic());
        assertEquals(POSITION, result.getPosition());
        assertEquals(DATE, result.getBirth());
        assertEquals(PHONE_NUMBER, result.getPhoneNumber());
        assertEquals(EMAIL, result.getEmail());

    }


    @Test
    void addDto_NotAllRequiredFields() {
        dto.setFirstname(null);
        dto.setLastname(null);
        dto.setBirth(null);
        assertThrows(NotAllRequiredFieldsPresentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_FirstnameLastnameBirthDate() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setBirth(DATE);

        result = personService.add(dto);

        assertNotNull(result);
        assertEquals(FIRSTNAME, result.getFirstname());
        assertEquals(LASTNAME, result.getLastname());
        assertEquals(DATE, result.getBirth());
    }

    @Test
    void addDto_FirstnameLastnameWithoutBirthDate() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setBirth(null);

        assertThrows(NotAllRequiredFieldsPresentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_FirstnameBirthDateWithoutLastname() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(null);
        dto.setBirth(DATE);

        assertThrows(NotAllRequiredFieldsPresentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_LastnameBirtDateWithoutFirstname() {
        dto.setFirstname(null);
        dto.setLastname(LASTNAME);
        dto.setBirth(DATE);

        assertThrows(NotAllRequiredFieldsPresentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDtoList() {
        //ok
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setPatronymic(PATRONYMIC);
        dto.setPosition(POSITION);
        dto.setBirth(DATE);
        dto.setPhoneNumber(PHONE_NUMBER);
        dto.setEmail(EMAIL);
        //ok
        PersonDTO dto1 = new PersonDTO();
        dto1.setFirstname("firstname1");
        dto1.setLastname("lastname1");
        dto1.setBirth(LocalDate.now());
        //not ok
        PersonDTO dto2 = new PersonDTO();
        dto2.setFirstname("firstname2");
        dto2.setLastname("lastname2");
        dto2.setBirth(null);
        //not ok
        PersonDTO dto3 = new PersonDTO();
        dto3.setFirstname("firstname3");
        dto3.setLastname(null);
        dto3.setBirth(LocalDate.now());
        //ok
        Person dto4

        List<PersonDTO> list = new LinkedList<>();
        list.add(dto);

    }


}