package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.services.interfaces.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class PersonServiceTest {

    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    private void clear() {
        userRepository.deleteAll();
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
    private final String EMAIL = "EMAIL@DOMAIN.EXT";

    @BeforeEach
    private void initializeVariables() {
        dto = new PersonDTO();
        result = null;
        entity = new PersonEntity();
    }

    @Test
    void addDto_AllFieldsCorrect() {
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
        dto.setPosition(null);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_FirstnameLastnameBirthDatePosition() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setBirth(DATE);
        dto.setPosition(POSITION);

        result = personService.add(dto);

        assertNotNull(result);
        assertEquals(FIRSTNAME, result.getFirstname());
        assertEquals(LASTNAME, result.getLastname());
        assertEquals(DATE, result.getBirth());
    }

    @Test
    void addDto_FirstnameLastnamePositionWithoutBirthDate() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setBirth(null);
        dto.setPosition(POSITION);

        assertThrows(IllegalArgumentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_FirstnameBirthDatePositionWithoutLastname() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(null);
        dto.setBirth(DATE);
        dto.setPosition(POSITION);

        assertThrows(IllegalArgumentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_LastnameBirtDatePositionWithoutFirstname() {
        dto.setFirstname(null);
        dto.setLastname(LASTNAME);
        dto.setBirth(DATE);
        dto.setPosition(POSITION);

        assertThrows(IllegalArgumentException.class,
                () -> personService.add(dto));
    }

    @Test
    void addDto_FirstnameLastnameBirtDateWithoutPosition() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setBirth(DATE);
        dto.setPosition(null);

        assertThrows(IllegalArgumentException.class,
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
        dto1.setPosition("position1");
        //not ok
        PersonDTO dto2 = new PersonDTO();
        dto2.setFirstname("firstname2");
        dto2.setLastname("lastname2");
        dto2.setBirth(null);
        dto2.setPosition("position2");
        //not ok
        PersonDTO dto3 = new PersonDTO();
        dto3.setFirstname("firstname3");
        dto3.setLastname(null);
        dto3.setBirth(LocalDate.now());
        dto3.setPosition("position3");
        //not ok
        PersonDTO dto4 = new PersonDTO();
        dto4.setFirstname(null);
        dto4.setLastname("lastname6");
        dto4.setBirth(LocalDate.now());
        dto4.setPosition("position6");
        //not ok
        PersonDTO dto5 = new PersonDTO();
        dto5.setFirstname("firstname4");
        dto5.setLastname("lastname4");
        dto5.setBirth(LocalDate.now());
        dto5.setPosition(null);
        //not ok
        PersonDTO dto6 = new PersonDTO();
        dto6.setFirstname(Strings.EMPTY);
        dto6.setLastname("lastname6");
        dto6.setBirth(LocalDate.now());
        dto6.setPosition("position6");
        //not ok
        PersonDTO dto7 = new PersonDTO();
        dto7.setFirstname("firstname3");
        dto7.setLastname(Strings.EMPTY);
        dto7.setBirth(LocalDate.now());
        dto7.setPosition("position3");
        //not ok
        PersonDTO dto8 = new PersonDTO();
        dto8.setFirstname("firstname4");
        dto8.setLastname("lastname4");
        dto8.setBirth(LocalDate.now());
        dto8.setPosition(Strings.EMPTY);
        //not ok
        PersonDTO dto9 = new PersonDTO();
        dto9.setFirstname(" ");
        dto9.setLastname("lastname6");
        dto9.setBirth(LocalDate.now());
        dto9.setPosition("position6");
        //not ok
        PersonDTO dto10 = new PersonDTO();
        dto10.setFirstname("firstname3");
        dto10.setLastname(" ");
        dto10.setBirth(LocalDate.now());
        dto10.setPosition("position3");
        //not ok
        PersonDTO dto11 = new PersonDTO();
        dto11.setFirstname("firstname4");
        dto11.setLastname("lastname4");
        dto11.setBirth(LocalDate.now());
        dto11.setPosition(" ");
        //not ok
        PersonDTO dto12 = new PersonDTO();
        dto12.setFirstname("\t");
        dto12.setLastname("lastname6");
        dto12.setBirth(LocalDate.now());
        dto12.setPosition("position6");
        //not ok
        PersonDTO dto13 = new PersonDTO();
        dto13.setFirstname("firstname3");
        dto13.setLastname("\t");
        dto13.setBirth(LocalDate.now());
        dto13.setPosition("position3");
        //not ok
        PersonDTO dto14 = new PersonDTO();
        dto14.setFirstname("firstname4");
        dto14.setLastname("lastname4");
        dto14.setBirth(LocalDate.now());
        dto14.setPosition("\t");
        //ok
        PersonDTO dto15 = new PersonDTO();
        dto15.setFirstname(FIRSTNAME);
        dto15.setLastname(LASTNAME);
        dto15.setBirth(DATE);
        dto15.setPosition(POSITION);

        List<PersonDTO> list = new LinkedList<>();
        list.addAll(Arrays.asList(
                dto, dto1, dto2, dto3, dto4, dto5, dto6, dto7, dto8,
                dto9, dto10, dto11, dto12, dto13, dto14, dto15));

        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto2);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto3);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto4);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto5);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto6);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto7);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto8);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto9);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto10);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto11);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto12);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto13);
        assertThrows(IllegalArgumentException.class,
                () -> personService.add(list));
        list.remove(dto14);

        List<PersonDTO> result = personService.add(list);

        assertNotNull(result);
        assertEquals(3, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertNotNull(result.get(i));
            assertNotNull(result.get(i).getId());
            assertTrue(result.get(i).getId() > 0);
            assertEquals(list.get(i).getFirstname(), result.get(i).getFirstname());
            assertEquals(list.get(i).getLastname(), result.get(i).getLastname());
            assertEquals(list.get(i).getPatronymic(), result.get(i).getPatronymic());
            assertEquals(list.get(i).getBirth(), result.get(i).getBirth());
            assertEquals(list.get(i).getPhoneNumber(), result.get(i).getPhoneNumber());
            assertEquals(list.get(i).getEmail(), result.get(i).getEmail());
            assertEquals(list.get(i).getPosition(), result.get(i).getPosition());
        }
    }

    private PersonDTO setFieldsAndSave() {
        dto.setFirstname(FIRSTNAME);
        dto.setLastname(LASTNAME);
        dto.setPatronymic(PATRONYMIC);
        dto.setPosition(POSITION);
        dto.setBirth(DATE);
        dto.setPhoneNumber(PHONE_NUMBER);
        dto.setEmail(EMAIL);
        return personService.add(dto);
    }

    @Test
    void updateDto_ExistingIdAllFieldsCorrect() {
        dto = setFieldsAndSave();
        dto.setFirstname("firstname1");
        dto.setLastname("lastname1");
        dto.setPatronymic("patronymic1");
        dto.setBirth(LocalDate.now());
        dto.setPosition("position1");
        dto.setPhoneNumber("+91234567890");
        dto.setEmail("email1@domain.ext");

        result = personService.update(dto);
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getFirstname(), result.getFirstname());
        assertEquals(dto.getLastname(), result.getLastname());
        assertEquals(dto.getPatronymic(), result.getPatronymic());
        assertEquals(dto.getBirth(), result.getBirth());
        assertEquals(dto.getPosition(), result.getPosition());
        assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void updateDTO_NotExistingIdAllFieldsCorrect() {
        dto = setFieldsAndSave();
        log.info(dto.toString());
        dto.setId(dto.getId() + 353);
        log.info(dto.toString());
        assertFalse(personRepository.existsById(dto.getId()));
        assertThrows(ResourceNotFoundException.class,
                () -> personService.update(dto));
    }


    @Test
    void updateDTO_NullIdAllFieldsCorrect() {
        dto = setFieldsAndSave();
        dto.setId(null);
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> personService.update(dto));
    }

    @Test
    void updateDto_AllRequiredFields() {
        dto = setFieldsAndSave();
        dto.setEmail(null);
        dto.setPatronymic(null);
        dto.setPhoneNumber(null);

        result = personService.update(dto);
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getFirstname(), result.getFirstname());
        assertEquals(dto.getLastname(), result.getLastname());
        assertEquals(dto.getPatronymic(), result.getPatronymic());
        assertEquals(dto.getBirth(), result.getBirth());
        assertEquals(dto.getPosition(), result.getPosition());
        assertEquals(dto.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(dto.getEmail(), result.getEmail());
    }

    @Test
    void updateDto_FirstnameLastnameBirthDateWithoutPosition() {
        dto = setFieldsAndSave();

        dto.setPosition(null);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setPosition(Strings.EMPTY);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setPosition(" ");
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setPosition("\t");
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
    }

    @Test
    void updateDto_FirstnamePositionBirthDateWithoutLastname() {
        dto = setFieldsAndSave();

        dto.setLastname(null);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setLastname(Strings.EMPTY);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setLastname(" ");
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setLastname("\t");
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
    }

    @Test
    void updateDto_LastnamePositionBirthDateWithoutFirstname() {
        dto = setFieldsAndSave();

        dto.setFirstname(null);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setFirstname(Strings.EMPTY);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setFirstname(" ");
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
        dto.setFirstname("\t");
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
    }

    @Test
    void updateDto_FirstnameLastnamePositionWithoutBirthDate() {
        dto = setFieldsAndSave();

        dto.setBirth(null);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(dto));
    }

    @Test
    void updateDtoList() {
        dto = setFieldsAndSave();
        PersonDTO anotherDto = new PersonDTO(dto);
        anotherDto.setId(null);
        anotherDto = personService.add(anotherDto);
        //ok
        dto.setFirstname(FIRSTNAME + "0");
        dto.setLastname(LASTNAME + "0");
        dto.setPatronymic(PATRONYMIC + "0");
        dto.setPosition(POSITION + "0");
        dto.setBirth(LocalDate.now());
        dto.setPhoneNumber("+81234567890");
        dto.setEmail("EMAIL0@DOMAIN.EXT");
        //ok
        PersonDTO dto1 = anotherDto;
        dto1.setFirstname("firstname1");
        dto1.setLastname("lastname1");
        dto1.setBirth(LocalDate.now());
        dto1.setPosition("position1");
        //not ok
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setFirstname("firstname2");
        dto2.setLastname("lastname2");
        dto2.setBirth(null);
        dto2.setPosition("position2");
        //not ok
        PersonDTO dto3 = new PersonDTO(dto);
        dto3.setFirstname("firstname3");
        dto3.setLastname(null);
        dto3.setBirth(LocalDate.now());
        dto3.setPosition("position3");
        //not ok
        PersonDTO dto4 = new PersonDTO(dto);
        dto4.setFirstname(null);
        dto4.setLastname("lastname6");
        dto4.setBirth(LocalDate.now());
        dto4.setPosition("position6");
        //not ok
        PersonDTO dto5 = new PersonDTO(dto);
        dto5.setFirstname("firstname4");
        dto5.setLastname("lastname4");
        dto5.setBirth(LocalDate.now());
        dto5.setPosition(null);
        //not ok
        PersonDTO dto6 = new PersonDTO(dto);
        dto6.setFirstname(Strings.EMPTY);
        dto6.setLastname("lastname6");
        dto6.setBirth(LocalDate.now());
        dto6.setPosition("position6");
        //not ok
        PersonDTO dto7 = new PersonDTO(dto);
        dto7.setFirstname("firstname3");
        dto7.setLastname(Strings.EMPTY);
        dto7.setBirth(LocalDate.now());
        dto7.setPosition("position3");
        //not ok
        PersonDTO dto8 = new PersonDTO(dto);
        dto8.setFirstname("firstname4");
        dto8.setLastname("lastname4");
        dto8.setBirth(LocalDate.now());
        dto8.setPosition(Strings.EMPTY);
        //not ok
        PersonDTO dto9 = new PersonDTO(dto);
        dto9.setFirstname(" ");
        dto9.setLastname("lastname6");
        dto9.setBirth(LocalDate.now());
        dto9.setPosition("position6");
        //not ok
        PersonDTO dto10 = new PersonDTO(dto);
        dto10.setFirstname("firstname3");
        dto10.setLastname(" ");
        dto10.setBirth(LocalDate.now());
        dto10.setPosition("position3");
        //not ok
        PersonDTO dto11 = new PersonDTO(dto);
        dto11.setFirstname("firstname4");
        dto11.setLastname("lastname4");
        dto11.setBirth(LocalDate.now());
        dto11.setPosition(" ");
        //not ok
        PersonDTO dto12 = new PersonDTO(dto);
        dto12.setFirstname("\t");
        dto12.setLastname("lastname6");
        dto12.setBirth(LocalDate.now());
        dto12.setPosition("position6");
        //not ok
        PersonDTO dto13 = new PersonDTO(dto);
        dto13.setFirstname("firstname3");
        dto13.setLastname("\t");
        dto13.setBirth(LocalDate.now());
        dto13.setPosition("position3");
        //not ok
        PersonDTO dto14 = new PersonDTO(dto);
        dto14.setFirstname("firstname4");
        dto14.setLastname("lastname4");
        dto14.setBirth(LocalDate.now());
        dto14.setPosition("\t");
        //not ok
        PersonDTO dto15 = new PersonDTO(dto);
        dto15.setId(null);
        dto15.setFirstname(FIRSTNAME);
        dto15.setLastname(LASTNAME);
        dto15.setBirth(DATE);
        dto15.setPosition(POSITION);
        //not ok
        PersonDTO dto16 = new PersonDTO(dto);
        dto16.setId(dto16.getId() + 355532);
        dto16.setFirstname(FIRSTNAME);
        dto16.setLastname(LASTNAME);
        dto16.setBirth(DATE);
        dto16.setPosition(POSITION);


        List<PersonDTO> list = new LinkedList<>();
        list.addAll(Arrays.asList(
                dto, dto1, dto2, dto3, dto4, dto5, dto6, dto7, dto8,
                dto9, dto10, dto11, dto12, dto13, dto14, dto15, dto16));

        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto2);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto3);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto4);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto5);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto6);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto7);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto8);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto9);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto10);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto11);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto12);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto13);
        assertThrows(IllegalArgumentException.class,
                () -> personService.update(list));
        list.remove(dto14);
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> personService.update(list));
        list.remove(dto15);
        assertThrows(ResourceNotFoundException.class,
                () -> personService.update(list));
        list.remove(dto16);


        List<PersonDTO> result = personService.update(list);

        assertNotNull(result);
        assertEquals(2, result.size());
        for (int i = 0; i < result.size(); i++) {
            assertNotNull(result.get(i));
            assertNotNull(result.get(i).getId());
            assertTrue(result.get(i).getId() > 0);
            assertEquals(list.get(i).getFirstname(), result.get(i).getFirstname());
            assertEquals(list.get(i).getLastname(), result.get(i).getLastname());
            assertEquals(list.get(i).getPatronymic(), result.get(i).getPatronymic());
            assertEquals(list.get(i).getBirth(), result.get(i).getBirth());
            assertEquals(list.get(i).getPhoneNumber(), result.get(i).getPhoneNumber());
            assertEquals(list.get(i).getEmail(), result.get(i).getEmail());
            assertEquals(list.get(i).getPosition(), result.get(i).getPosition());
        }
    }

    @Test
    void deleteId_CorrectIdNoUser() {
        dto = setFieldsAndSave();
        assertFalse(userRepository.existsByPerson_Id(dto.getId()));
        result = personService.delete(dto.getId());
        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void deleteId_NullId() {
        dto = setFieldsAndSave();
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> personService.delete(null));
    }

    @Test
    void deleteId_NotExistingId() {
        dto = setFieldsAndSave();
        dto.setId(dto.getId() + 32434434);
        assertFalse(personRepository.existsById(dto.getId()));
        assertThrows(ResourceNotFoundException.class,
                () -> personService.delete(dto.getId()));
    }

    @Test
    void deleteId_CorrectIdRegisteredUser(){
        dto = setFieldsAndSave();
        entity = personRepository.getById(dto.getId());
        UserEntity user = new UserEntity();
        user.setPerson(entity);
        user.setUsername("username");
        user.setPassword(new BCryptPasswordEncoder(11).encode("password"));
        user = userRepository.save(user);
        assertNotNull(user);
        assertTrue(userRepository.existsByPerson_Id(dto.getId()));
        assertThrows(ProhibitedRemovingException.class,
                () -> personService.delete(dto.getId()));
    }

    @Test
    void getById_ExistingId() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        result = personService.getById(dto.getId());
        assertNotNull(result);
        assertEquals(dto, result);
    }

    @Test
    void getById_NotExistingId() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        Long id = dto.getId() + 23423;
        assertFalse(personRepository.existsById(id));
        assertThrows(ResourceNotFoundException.class,
                () -> personService.getById(id));
    }

    @Test
    void getById_NullId() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> personService.getById(null));
    }

    @Test
    void getEntityById_ExistingId() {
        dto = setFieldsAndSave();
        entity = personRepository.getById(dto.getId());
        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        PersonEntity result = personService.getEntityById(dto.getId());
        assertNotNull(result);
        assertEquals(entity, result);
    }

    @Test
    void getEntityById_NotExistingId() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        Long id = dto.getId() + 23423;
        assertFalse(personRepository.existsById(id));
        assertThrows(ResourceNotFoundException.class,
                () -> personService.getEntityById(id));
    }

    @Test
    void getEntityById_NullId() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> personService.getEntityById(null));
    }

    @Test
    void getAll() {
        dto = setFieldsAndSave();
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setId(null);
        dto2.setFirstname("firstname2");
        dto2.setLastname("lastname2");
        dto2.setBirth(LocalDate.now());
        dto2.setPosition("position2");
        dto2.setPatronymic(null);
        dto2.setPhoneNumber(null);
        dto2.setEmail(null);
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto.getId()));
        assertTrue(personRepository.existsById(dto2.getId()));
        //log.info
        for (PersonEntity e : personRepository.findAll()) {
            log.info(e.toString());
        }
        List<PersonDTO> result = personService.getAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto));
        assertTrue(result.contains(dto2));
    }

    @Test
    void findAllByFirstname_Exist() {
        dto = setFieldsAndSave();
        PersonDTO dto1 = new PersonDTO(dto);
        dto1.setLastname("lastname1");
        dto1 = personService.add(dto1);
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setFirstname("firstname2");
        dto2 = personService.add(dto2);

        List<PersonDTO> result = personService.findAllByFirstname(dto.getFirstname());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto));
        assertTrue(result.contains(dto1));
        assertFalse(result.contains(dto2));
    }

    @Test
    void findAllByFirstname_Blank() {
        dto = setFieldsAndSave();
        PersonDTO dto1 = new PersonDTO(dto);
        dto1.setLastname("lastname1");
        dto1 = personService.add(dto1);
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setFirstname("firstname2");
        dto2 = personService.add(dto2);

        List<PersonDTO> result = personService.findAllByFirstname(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        result = personService.findAllByFirstname(Strings.EMPTY);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        result = personService.findAllByFirstname(" ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        result = personService.findAllByFirstname("\t");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByFirstname_NotExist() {
        dto = setFieldsAndSave();
        PersonDTO dto1 = new PersonDTO(dto);
        dto1.setLastname("lastname1");
        dto1 = personService.add(dto1);
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setFirstname("firstname2");
        dto2 = personService.add(dto2);

        String firstname = "something";
        assertFalse(Strings.isBlank(firstname));
        assertNotEquals(dto.getFirstname(), firstname);
        assertNotEquals(dto1.getFirstname(), firstname);
        assertNotEquals(dto2.getFirstname(), firstname);

        List<PersonDTO> result = personService.findAllByFirstname(firstname);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByLastname_Exist() {
        dto = setFieldsAndSave();
        log.info(dto.toString());
        PersonDTO dto1 = new PersonDTO(dto);
        dto1.setFirstname("firstname1");
        dto1 = personService.add(dto1);
        log.info(dto1.toString());
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setLastname("lastname2");
        dto2 = personService.add(dto2);
        log.info(dto2.toString());

        //log.info
        personRepository.findAll().forEach(e -> log.info(e.toString()));

        List<PersonDTO> result = personService.findAllByLastname(dto.getLastname());
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto));
        assertTrue(result.contains(dto1));
        assertFalse(result.contains(dto2));
    }

    @Test
    void findAllByLastname_Blank() {
        dto = setFieldsAndSave();
        PersonDTO dto1 = new PersonDTO(dto);
        dto1.setFirstname("firstname1");
        dto1 = personService.add(dto1);
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setLastname("lastname2");
        dto2 = personService.add(dto2);

        List<PersonDTO> result = personService.findAllByLastname(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        result = personService.findAllByLastname(Strings.EMPTY);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        result = personService.findAllByLastname(" ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        result = personService.findAllByLastname("\t");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByLastname_NotExist() {
        dto = setFieldsAndSave();
        PersonDTO dto1 = new PersonDTO(dto);
        dto1.setFirstname("firstname1");
        dto1 = personService.add(dto1);
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setLastname("lastname2");
        dto2 = personService.add(dto2);

        String lastname = "something";
        assertFalse(Strings.isBlank(lastname));
        assertNotEquals(dto.getLastname(), lastname);
        assertNotEquals(dto1.getLastname(), lastname);
        assertNotEquals(dto2.getLastname(), lastname);

        List<PersonDTO> result = personService.findAllByLastname(lastname);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByBirthDate_Exist() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setBirth(dto.getBirth().minusDays(34));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllByBirthDate(dto.getBirth());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(dto));
        assertFalse(result.contains(dto2));
    }

    @Test
    void findAllByBirthDate_NotExist() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setBirth(dto.getBirth().minusDays(34));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        LocalDate date = dto.getBirth().minusDays(1234);
        assertEquals(0, personRepository.findAllByBirthEquals(date).size());

        List<PersonDTO> result = personService.findAllByBirthDate(date);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void findAllByBirthDate_Null() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setBirth(dto.getBirth().minusDays(34));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllByBirthDate(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void findAllOlderThan_Exist() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        int diff = 22;
        dto2.setBirth(dto.getBirth().minusYears(diff));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllOlderThan(diff - 2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.contains(dto));
        assertTrue(result.contains(dto2));
    }

    @Test
    void findAllOlderThan_NotExist() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        int diff = 22;
        dto2.setBirth(dto.getBirth().minusYears(diff));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        Integer age = 50;
        LocalDate date = LocalDate.now().minusYears(50);
        assertEquals(0, personRepository.findAllByBirthBefore(date).size());

        List<PersonDTO> result = personService.findAllOlderThan(age);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void findAllOlderThan_Null() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        dto2.setBirth(dto.getBirth().minusYears(22));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllOlderThan(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void findAllYoungerThan_Exist() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        int diff = 22;
        dto2.setBirth(dto.getBirth().minusYears(diff));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllOlderThan(diff-2);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(dto));
        assertFalse(result.contains(dto2));
    }

    @Test
    void findAllYoungerThan_NotExist() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        personService.delete(dto.getId());
        assertFalse(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        int diff = 22;
        dto2.setBirth(dto.getBirth().minusYears(diff));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllOlderThan(diff - 2);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void findAllYoungerThan_Null() {
        dto = setFieldsAndSave();
        assertTrue(personRepository.existsById(dto.getId()));
        PersonDTO dto2 = new PersonDTO(dto);
        int diff = 22;
        dto2.setBirth(dto.getBirth().minusYears(diff));
        dto2 = personService.add(dto2);
        assertTrue(personRepository.existsById(dto2.getId()));

        List<PersonDTO> result = personService.findAllOlderThan(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
























