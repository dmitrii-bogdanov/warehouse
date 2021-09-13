package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.mappers.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RoleServiceTest extends AbstractSpringBootTest {

    @Autowired
    protected RoleService roleService;
    @Autowired
    private ObjectMapper objectMapper;

    private final Collection<RoleEntity> ROLES = Arrays.stream(Role.values()).map(RoleEntity::new).toList();

    private final String NOT_RECORDED_NAME = "ROLE_SOMETHING";
    private final String NULL_NAME = null;
    private final String EMPTY_NAME = Strings.EMPTY;
    private final String SPACE_NAME = " ";
    private final String BLANK_NAME = "\t   \t ";

    private List<RoleEntity> entitiesList;
    private RoleEntity entity;
    private List<RoleDTO> dtoList;
    private RoleDTO dto;
    private Collection<String> names;

    @BeforeEach
    void initializeVariables() {
        dto = null;
        dtoList = null;
        entity = null;
        entitiesList = null;
        names = new LinkedList<>();
    }

    @BeforeEach
    void checkRolesSize() {
        assertFalse(ROLES.isEmpty());
    }

    private RoleDTO convert(RoleEntity entity) {
        return objectMapper.convertValue(entity, RoleDTO.class);
    }

    @Test
    void getAllEntities() {
        entitiesList = roleService.getAllEntities();
        assertEquals(ROLES.size(), entitiesList.size());
        assertTrue(entitiesList.containsAll(ROLES));
    }

    @Test
    void getAll() {
        dtoList = roleService.getAll();
        assertEquals(ROLES.size(), dtoList.size());
        assertTrue(dtoList.containsAll(ROLES.stream().map(this::convert).toList()));
    }

    @Test
    void getEntityByName() {
        for (RoleEntity role : ROLES) {
            assertEquals(role, roleService.getEntityByName(role.getName().toLowerCase(Locale.ROOT)));
        }
    }

    @Test
    void getEntityByName_NotRecordedName() {
        final String name = NOT_RECORDED_NAME;
        ROLES.forEach(r -> assertFalse(r.getName().equalsIgnoreCase(name)));
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> roleService.getEntityByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getEntityByName_NullName() {
        final String name = NULL_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_EmptyName() {
        final String name = EMPTY_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_SpaceName() {
        final String name = SPACE_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getEntityByName_BlankName() {
        final String name = BLANK_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getEntityByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName() {
        for (RoleEntity role : ROLES) {
            assertEquals(convert(role), roleService.getByName(role.getName().toLowerCase(Locale.ROOT)));
        }
    }

    @Test
    void getByName_NotRecordedName() {
        final String name = NOT_RECORDED_NAME;
        ROLES.forEach(r -> assertFalse(r.getName().equalsIgnoreCase(name)));
        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> roleService.getByName(name));
        assertEquals(ExceptionType.RESOURCE_NOT_FOUND, e.getExceptionType());
    }

    @Test
    void getByName_NullName() {
        final String name = NULL_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_EmptyName() {
        final String name = EMPTY_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_SpaceName() {
        final String name = SPACE_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

    @Test
    void getByName_BlankName() {
        final String name = BLANK_NAME;
        ArgumentException e = assertThrows(ArgumentException.class,
                () -> roleService.getByName(name));
        assertEquals(ExceptionType.BLANK_ENTITY_NAME, e.getExceptionType());
    }

}