package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public RoleMapper roleMapper;

    //region Autowired setters
    @Autowired
    private void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }
    //endregion

    public RoleEntity convert(RoleDTO role) {
        return roleMapper.convert(role);
    }

    public RoleDTO convert(RoleEntity role) {
        return roleMapper.convert(role);
    }

}
