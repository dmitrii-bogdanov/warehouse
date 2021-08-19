package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.dto.RoleDTO;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    RoleEntity convert(RoleDTO role) {
        return new RoleEntity(role.getId(), role.getName());
    }

    RoleDTO convert(RoleEntity role) {
        return new RoleDTO(role.getId(), role.getName());
    }

}
