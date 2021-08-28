package bogdanov.warehouse.database.entities;

import bogdanov.warehouse.database.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest {

    private RoleEntity entity;

    @BeforeEach
    private void initializeVariable() {
        entity = null;
    }

    @Test
    void constructorRoleEnum() {
        for (Role role : Role.values()) {
            entity = new RoleEntity(role);
            assertNotNull(entity);
            assertEquals(role.name(), entity.getName());
            assertEquals(role.getId(), entity.getId());
        }
    }

    @Test
    void getAuthority() {
        entity = new RoleEntity();
        String name = "admin";
        entity.setName(name);
        assertEquals(name.toUpperCase(Locale.ROOT), entity.getAuthority());
    }

}