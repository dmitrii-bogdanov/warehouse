package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.repositories.RecordTypeRepository;
import bogdanov.warehouse.dto.RecordTypeDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.assertj.core.util.diff.Delta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecordTypeServiceTest extends AbstractSpringBootTest {

    @Autowired
    private RecordTypeService recordTypeService;
    @Autowired
    private RecordTypeRepository recordTypeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final List<RecordTypeEntity> TYPES = Arrays.stream(RecordType.values()).map(RecordTypeEntity::new).toList();
    private final String NOT_REGISTERED_NAME = "SOMETHING";
    private final Long NOT_REGISTERED_ID = 444L;
    private final String NULL_NAME = null;
    private final String EMPTY_NAME = Strings.EMPTY;
    private final String SPACE_NAME = " ";
    private final String BLANK_NAME = "\t  \t \t\t ";

    private RecordTypeEntity entity;
    private List<RecordTypeEntity> entitiesList;
    private RecordTypeDTO dto;
    private List<RecordTypeDTO> dtoList;

    @BeforeEach
    private void initializeVariables() {
        entity = null;
        entitiesList = null;
        dto = null;
        dtoList = null;
    }

    private RecordTypeDTO convert(RecordTypeEntity entity){
        return objectMapper.convertValue(entity, RecordTypeDTO.class);
    }

    @Test
    void getAll() {
        dtoList = recordTypeService.getAll();
        assertNotNull(dtoList);
        assertEquals(TYPES.size(), dtoList.size());
        assertTrue(dtoList.containsAll(TYPES.stream().map(this::convert).toList()));
    }

    @Test
    void getEntityById() {
        TYPES.forEach(type -> assertEquals(type, recordTypeService.getEntityById(type.getId())));
    }

    @Test
    void getEntityById_NotRegisteredId() {
        final Long id = NOT_REGISTERED_ID;
        assertFalse(recordTypeRepository.existsById(id));
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordTypeService.getEntityById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getEntityById_NullId() {
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getEntityById(null));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

    @Test
    void getById() {
        TYPES.forEach(type -> assertEquals(convert(type), recordTypeService.getById(type.getId())));
    }

    @Test
    void getById_NotRegisteredId() {
        final Long id = NOT_REGISTERED_ID;
        assertFalse(recordTypeRepository.existsById(id));
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordTypeService.getById(id));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getById_NullId() {
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getById(null));
        assertEquals(ExceptionType.NULL_ID, e.getExceptionType());
    }

    @Test
    void getEntityByName() {
        TYPES.forEach(type -> assertEquals(type, recordTypeService.getEntityByName(type.getName())));
    }

    @Test
    void getEntityByName_NotRegisteredName() {
        final String name = NOT_REGISTERED_NAME;
        assertTrue(recordTypeRepository.findByNameIgnoreCase(name).isEmpty());
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordTypeService.getEntityByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getEntityByName_NullName() {
        final String name = NULL_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_EmptyName() {
        final String name = EMPTY_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_SpaceName() {
        final String name = SPACE_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_BlankName() {
        final String name = BLANK_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName() {
        TYPES.forEach(type -> assertEquals(convert(type), recordTypeService.getByName(type.getName())));
    }

    @Test
    void getByName_NotRegisteredName() {
        final String name = NOT_REGISTERED_NAME;
        assertTrue(recordTypeRepository.findByNameIgnoreCase(name).isEmpty());
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> recordTypeService.getByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getByName_NullName() {
        final String name = NULL_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_EmptyName() {
        final String name = EMPTY_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_SpaceName() {
        final String name = SPACE_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_BlankName() {
        final String name = BLANK_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> recordTypeService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

}