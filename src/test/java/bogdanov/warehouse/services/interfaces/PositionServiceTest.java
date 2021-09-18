package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ProhibitedRemovingException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class PositionServiceTest {

    @Autowired
    private PositionService positionService;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private List<PositionDTO> dtoList;
    private List<PositionDTO> resultList;
    private PositionDTO dto;
    private PositionDTO result;
    private PositionEntity resultEntity;
    private List<PositionEntity> resultEntityList;

    private final String POSITION = "Position";
    private final String ID = "id";
    private final String NAME = "name";

    @BeforeEach
    private void clear() {
        personRepository.deleteAll();
        positionRepository.deleteAll();
    }

    @BeforeEach
    private void initializeVariables() {
        dtoList = new LinkedList<>();
        resultList = null;
        dto = new PositionDTO(null, "admin");
        result = null;
        resultEntity = null;
        resultEntityList = null;
    }

    @Test
    void addString_FirstEntry() {
        resultEntity = positionService.add(dto.getName());
        assertNotNull(resultEntity);
        assertNotNull(resultEntity.getId());
        assertTrue(resultEntity.getId() > 0);
        assertEquals(dto.getName().toUpperCase(Locale.ROOT), resultEntity.getName());
    }

    @Test
    void addString_SecondEntryWithNotRegisteredName() {
        dtoList.add(dto);// 0
        dtoList.add(new PositionDTO(null, "user"));// 1
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultEntityList = new LinkedList<>();
        resultEntityList.add(positionService.add(dtoList.get(0).getName()));
        resultEntityList.add(positionService.add(dtoList.get(1).getName()));
        assertNotNull(resultEntityList.get(0));
        assertNotNull(resultEntityList.get(1));
        assertNotNull(resultEntityList.get(0).getId());
        assertNotNull(resultEntityList.get(1).getId());
        assertTrue(resultEntityList.get(0).getId() > 0);
        assertTrue(resultEntityList.get(1).getId() > 0);
        assertEquals(dtoList.get(0).getName().toUpperCase(Locale.ROOT), resultEntityList.get(0).getName().toUpperCase(Locale.ROOT));
        assertEquals(dtoList.get(1).getName().toUpperCase(Locale.ROOT), resultEntityList.get(1).getName().toUpperCase(Locale.ROOT));
        assertNotEquals(resultEntityList.get(0), resultEntityList.get(1));
    }

    @Test
    void addString_SecondEntryWithAlreadyRegistered() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, dto.getName()));
        assertEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultEntityList = new LinkedList<>();
        resultEntityList.add(positionService.add(dtoList.get(0).getName()));
        resultEntityList.add(positionService.add(dtoList.get(1).getName()));
        assertNotNull(resultEntityList.get(0));
        assertNotNull(resultEntityList.get(1));
        assertEquals(resultEntityList.get(0), resultEntityList.get(1));
    }

    @Test
    void addString_NullName() {
        dto.setName(null);
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addString_EmptyName() {
        dto.setName(Strings.EMPTY);
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addString_SpaceName() {
        dto.setName(" ");
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addString_TabName() {
        dto.setName("\t");
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto.getName()));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addDto_FirstEntry() {
        result = positionService.add(dto);
        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getId() > 0);
        assertEquals(dto.getName().toUpperCase(), result.getName());
    }


    @Test
    void addDto_SecondEntryWithNotRegisteredName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, "user"));
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0)));
        resultList.add(positionService.add(dtoList.get(1)));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertNotNull(resultList.get(0).getId());
        assertNotNull(resultList.get(1).getId());
        assertTrue(resultList.get(0).getId() > 0);
        assertTrue(resultList.get(1).getId() > 0);
        assertNotEquals(resultList.get(0), resultList.get(1));
        assertEquals(dtoList.get(0).getName().toUpperCase(), resultList.get(0).getName());
        assertEquals(dtoList.get(1).getName().toUpperCase(), resultList.get(1).getName());
    }

    @Test
    void addDto_SecondEntryWithAlreadyRegistered() {
        dtoList.add(dto);//0
        dtoList.add(new PositionDTO(null, dto.getName()));//1
        assertEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        resultList = new LinkedList<>();
        resultList.add(positionService.add(dtoList.get(0)));
        resultList.add(positionService.add(dtoList.get(1)));
        assertNotNull(resultList.get(0));
        assertNotNull(resultList.get(1));
        assertEquals(resultList.get(0), resultList.get(1));
    }

    @Test
    void addDto_NullName() {
        dto.setName(null);
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addDto_EmptyName() {
        dto.setName(Strings.EMPTY);
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addDto_SpaceName() {
        dto.setName(" ");
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addDto_TabName() {
        dto.setName("\t");
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dto));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void addListDto() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, "user"));
        dtoList.add(new PositionDTO(null, dtoList.get(1).getName()));
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        assertEquals(dtoList.get(1).getName(), dtoList.get(2).getName());
        resultList = positionService.add(dtoList);
        assertNotNull(resultList);
        assertEquals(dtoList.size(), resultList.size());
        for (PositionDTO r : resultList) {
            assertNotNull(r);
            assertNotNull(r.getId());
            assertTrue(r.getId() > 0);
        }
        for (int i = 0; i < dtoList.size(); i++) {
            assertEquals(dtoList.get(i).getName().toUpperCase(), resultList.get(i).getName());
        }
        assertNotEquals(resultList.get(0), resultList.get(1));
        assertEquals(resultList.get(1), resultList.get(2));
    }

    @Test
    void addListDto_NullName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, null));
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dtoList));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(0, positionRepository.findAll().size());
    }

    @Test
    void addListDto_EmptyName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, Strings.EMPTY));
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dtoList));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(0, positionRepository.findAll().size());
    }


    @Test
    void addListDto_SpaceName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, " "));
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dtoList));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(0, positionRepository.findAll().size());
    }

    @Test
    void addListDto_TabName() {
        dtoList.add(dto);
        dtoList.add(new PositionDTO(null, "\t"));
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.add(dtoList));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
        assertEquals(0, positionRepository.findAll().size());
    }

    @Test
    void getAll() {
        dtoList.add(dto);//0
        dtoList.add(new PositionDTO(null, "staff"));//1
        dtoList.add(new PositionDTO(null, dtoList.get(1).getName()));//2
        dtoList.add(new PositionDTO(null, "user"));//3
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(1).getName());
        assertEquals(dtoList.get(1).getName(), dtoList.get(2).getName());
        assertNotEquals(dtoList.get(0).getName(), dtoList.get(3).getName());
        assertNotEquals(dtoList.get(1).getName(), dtoList.get(3).getName());
        dtoList = positionService.add(dtoList).stream().distinct().toList();
        resultList = positionService.getAll();
        assertNotNull(resultList);
        assertEquals(dtoList.size(), resultList.size());
        for (PositionDTO r : resultList) {
            assertNotNull(r);
            assertNotNull(r.getId());
            assertTrue(r.getId() > 0);
        }
        assertTrue(resultList.containsAll(dtoList));
        assertEquals(resultList.size(), resultList.stream().distinct().toList().size());
    }

    @Test
    void getEntityById() {
        PositionEntity entity1 = new PositionEntity("ADMIN");
        PositionEntity entity2 = new PositionEntity("USER");
        entity1 = positionRepository.save(entity1);
        entity2 = positionRepository.save(entity2);
        assertTrue(positionRepository.existsById(entity1.getId()));
        assertTrue(positionRepository.existsById(entity2.getId()));

        assertEquals(entity1, positionService.getEntityById(entity1.getId()));
        assertEquals(entity2, positionService.getEntityById(entity2.getId()));
    }


    @Test
    void getEntityById_WrongId() {
        PositionEntity entity = new PositionEntity("ADMIN");
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        Long id = entity.getId() + 33;
        assertFalse(positionRepository.existsById(id));

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> positionService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }


    @Test
    void getEntityById_NullId() {
        PositionEntity entity = new PositionEntity("ADMIN");
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityById(null));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

    @Test
    void getById() {
        PositionEntity entity1 = new PositionEntity("ADMIN");
        PositionEntity entity2 = new PositionEntity("USER");
        PositionDTO dto1 = objectMapper.convertValue(positionRepository.save(entity1), PositionDTO.class);
        PositionDTO dto2 = objectMapper.convertValue(positionRepository.save(entity2), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto1.getId()));
        assertTrue(positionRepository.existsById(dto2.getId()));

        assertEquals(dto1, positionService.getById(dto1.getId()));
        assertEquals(dto2, positionService.getById(dto2.getId()));
    }


    @Test
    void getById_WrongId() {
        PositionEntity entity = new PositionEntity("ADMIN");
        dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto.getId()));

        Long id = dto.getId() + 33;
        assertFalse(positionRepository.existsById(id));

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> positionService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }


    @Test
    void getById_NullId() {
        PositionEntity entity = new PositionEntity("ADMIN");
        dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto.getId()));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityById(null));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

    @Test
    void getEntityByName() {
        PositionEntity entity1 = new PositionEntity("ADMIN".toUpperCase(Locale.ROOT));
        PositionEntity entity2 = new PositionEntity("USER".toUpperCase(Locale.ROOT));

        entity1 = positionRepository.save(entity1);
        entity2 = positionRepository.save(entity2);

        assertTrue(positionRepository.findByNameIgnoreCase(entity1.getName()).isPresent());
        assertTrue(positionRepository.findByNameIgnoreCase(entity2.getName()).isPresent());

        assertEquals(entity1, positionService.getEntityByName(entity1.getName().toLowerCase(Locale.ROOT)));
        assertEquals(entity2, positionService.getEntityByName(entity2.getName().toLowerCase(Locale.ROOT)));
    }

    @Test
    void getEntityByName_NotRegisteredName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.findByNameIgnoreCase(entity.getName()).isPresent());

        final String name = "admin";

        assertTrue(positionRepository.findByNameIgnoreCase(name).isEmpty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> positionService.getEntityByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getEntityByName_NullName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.findByNameIgnoreCase(entity.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName(null));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_EmptyName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.findByNameIgnoreCase(entity.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName(Strings.EMPTY));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_SpaceName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.findByNameIgnoreCase(entity.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName(" "));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_TabName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.findByNameIgnoreCase(entity.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName("\t"));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName() {
        PositionEntity entity1 = new PositionEntity("ADMIN".toUpperCase(Locale.ROOT));
        PositionEntity entity2 = new PositionEntity("USER".toUpperCase(Locale.ROOT));

        PositionDTO dto1 = objectMapper.convertValue(positionRepository.save(entity1), PositionDTO.class);
        PositionDTO dto2 = objectMapper.convertValue(positionRepository.save(entity2), PositionDTO.class);

        assertTrue(positionRepository.findByNameIgnoreCase(dto1.getName()).isPresent());
        assertTrue(positionRepository.findByNameIgnoreCase(dto2.getName()).isPresent());

        assertEquals(dto1, positionService.getByName(dto1.getName().toLowerCase(Locale.ROOT)));
        assertEquals(dto2, positionService.getByName(dto2.getName().toLowerCase(Locale.ROOT)));
    }

    @Test
    void getByName_NotRegisteredName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.findByNameIgnoreCase(dto.getName()).isPresent());

        final String name = "admin";

        assertTrue(positionRepository.findByNameIgnoreCase(name).isEmpty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> positionService.getEntityByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getByName_NullName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.findByNameIgnoreCase(dto.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName(null));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_EmptyName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.findByNameIgnoreCase(dto.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName(Strings.EMPTY));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_SpaceName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.findByNameIgnoreCase(dto.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName(" "));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_TabName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.findByNameIgnoreCase(dto.getName()).isPresent());

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.getEntityByName("\t"));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void findAllByNameContaining() {
        PositionEntity entity1 = new PositionEntity("ADMIN".toUpperCase(Locale.ROOT));
        PositionEntity entity2 = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionEntity entity3 = new PositionEntity("ADMINISTRATOR".toUpperCase(Locale.ROOT));

        PositionDTO dto1 = objectMapper.convertValue(positionRepository.save(entity1), PositionDTO.class);
        PositionDTO dto2 = objectMapper.convertValue(positionRepository.save(entity2), PositionDTO.class);
        PositionDTO dto3 = objectMapper.convertValue(positionRepository.save(entity3), PositionDTO.class);

        assertTrue(positionRepository.existsById(entity1.getId()));
        assertTrue(positionRepository.existsById(entity2.getId()));
        assertTrue(positionRepository.existsById(entity3.getId()));

        resultList = positionService.findAllByNameContaining("MIN".toLowerCase(Locale.ROOT));
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(dto1));
        assertFalse(resultList.contains(dto2));
        assertTrue(resultList.contains(dto3));

        resultList = null;
        resultList = positionService.findAllByNameContaining("SE".toLowerCase(Locale.ROOT));
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertFalse(resultList.contains(dto1));
        assertTrue(resultList.contains(dto2));
        assertFalse(resultList.contains(dto3));

        resultList = null;
        resultList = positionService.findAllByNameContaining("ADMINIST".toLowerCase(Locale.ROOT));
        assertNotNull(resultList);
        assertEquals(1, resultList.size());
        assertFalse(resultList.contains(dto1));
        assertFalse(resultList.contains(dto2));
        assertTrue(resultList.contains(dto3));

        resultList = null;
        resultList = positionService.findAllByNameContaining("SOMETHING_OTHER".toLowerCase(Locale.ROOT));
        assertNotNull(resultList);
        assertEquals(0, resultList.size());
    }

    @Test
    void findAllByNameContaining_NullName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.findAllByNameContaining(null));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void findAllByNameContaining_EmptyName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.findAllByNameContaining(Strings.EMPTY));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void findAllByNameContaining_SpaceName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.findAllByNameContaining(" "));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void findAllByNameContaining_TabName() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.findAllByNameContaining("\t"));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void deleteById() {
        PositionEntity entity1 = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto1 = objectMapper.convertValue(positionRepository.save(entity1), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto1.getId()));

        PositionEntity entity2 = new PositionEntity("ADMIN".toUpperCase(Locale.ROOT));
        PositionDTO dto2 = objectMapper.convertValue(positionRepository.save(entity2), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto2.getId()));

        assertEquals(dto1, positionService.delete(dto1.getId()));
        assertFalse(positionRepository.existsById(dto1.getId()));
        assertTrue(positionRepository.existsById(dto2.getId()));

        assertEquals(dto2, positionService.delete(dto2.getId()));
        assertFalse(positionRepository.existsById(dto2.getId()));
    }

    @Test
    void deleteById_PositionIsInUse() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        entity = positionRepository.save(entity);
        assertTrue(positionRepository.existsById(entity.getId()));

        PersonEntity person = new PersonEntity();
        person.setFirstname("FIRSTNAME");
        person.setLastname("LASTNAME");
        person.setBirth(LocalDate.now());
        person.setPosition(entity);
        person = personRepository.save(person);

        Long id = entity.getId();

        ProhibitedRemovingException e = assertThrows(ProhibitedRemovingException.class,
                () -> positionService.delete(id));
        assertEquals(ExceptionType.POSITION_IS_IN_USE, e.getExceptionType());

        personRepository.delete(person);
        dto = objectMapper.convertValue(entity, PositionDTO.class);

        assertEquals(dto, positionService.delete(entity.getId()));
        assertFalse(positionRepository.existsById(entity.getId()));
    }

    @Test
    void deleteById_NotRegisteredId() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto.getId()));

        final Long id = dto.getId() + 33;
        assertFalse(positionRepository.existsById(id));

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> positionService.delete(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void deleteById_NullId() {
        PositionEntity entity = new PositionEntity("USER".toUpperCase(Locale.ROOT));
        PositionDTO dto = objectMapper.convertValue(positionRepository.save(entity), PositionDTO.class);
        assertTrue(positionRepository.existsById(dto.getId()));

        final Long id = null;

        ArgumentException e = assertThrows(ArgumentException.class,
                () -> positionService.delete(id));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

}















