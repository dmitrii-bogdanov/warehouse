package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class RoleServiceTest {

    @Autowired
    protected RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private Mapper mapper;

    private final List<RoleEntity> entities = List.of(
            new RoleEntity(1, "role_admin"),
            new RoleEntity(2, "role_staff"),
            new RoleEntity(3, "role_user")
    );

    private RoleEntity entity;
    private RoleDTO dto;

    @BeforeEach
    private void initializeVariables() {
        entity = null;
        dto = null;
    }


    @Test
    void getAll() {
        List<RoleDTO> result = roleService.getAll();
        assertNotNull(result);
        for (RoleEntity role : entities) {
            assertTrue(result.contains(mapper.convert(role)));
        }
    }

    @Test
    void findByName_existingName() {
        for (RoleEntity role : entities) {
            dto = roleService.findByName(role.getName());
            assertNotNull(dto);
            assertEquals(mapper.convert(role), dto);
            dto = null;
        }
    }

    @Test
    void findByNameString_notExistingName() {
        String name = "something";
        assertThrows(ResourceNotFoundException.class,
                () -> roleService.findByName(name));
    }

    @Test
    void findByNameString_BlankName() {
        final String name = null;
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName(name));
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName(Strings.EMPTY));
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName(" "));
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName("\t"));

    }

    @Test
    void findByNameCollection() {
        List<String> list = new LinkedList<>();
        list.add(entities.get(0).getName());
        list.add(entities.get(2).getName());
        list.add("something");
        list.add(null);

        List<RoleDTO> result = roleService.findByName(list);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(result.contains(null));
        assertTrue(result.contains(mapper.convert(entities.get(0))));
        assertFalse(result.contains(mapper.convert(entities.get(1))));
        assertTrue(result.contains(mapper.convert(entities.get(2))));
    }

    @Test
    void getAllEntities() {
        List<RoleEntity> list = roleService.getAllEntities();
        assertNotNull(list);
        for (RoleEntity role : entities) {
            assertTrue(list.contains(role));
        }
    }

    @Test
    void findEntityByName_existingName() {
        for (RoleEntity role : entities) {
            entity = roleService.findEntityByName(role.getName());
            assertNotNull(entities);
            assertEquals(role, entity);
            entity = null;
        }
    }

    @Test
    void findEntityByNameString_notExistingName() {
        String name = "something";
        assertThrows(ResourceNotFoundException.class,
                () -> roleService.findEntityByName(name));
    }

    @Test
    void findEntityByNameString_BlankName() {
        final String name = null;
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName(name));
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName(Strings.EMPTY));
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName(" "));
        assertThrows(IllegalArgumentException.class,
                () -> roleService.findByName("\t"));

    }

    @Test
    void findEntitiesByNameCollection() {
        List<String> list = new LinkedList<>();
        list.add(entities.get(0).getName());
        list.add(entities.get(2).getName());
        list.add("something");
        list.add(null);

        List<RoleEntity> result = roleService.findEntitiesByName(list);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(result.contains(null));
        assertTrue(result.contains(entities.get(0)));
        assertFalse(result.contains(entities.get(1)));
        assertTrue(result.contains(entities.get(2)));
    }

}