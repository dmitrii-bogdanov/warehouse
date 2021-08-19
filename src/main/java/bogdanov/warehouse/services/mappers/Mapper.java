package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private RoleMapper roleMapper;
    private UserRegistrationMapper userRegistrationMapper;

    //region Autowired setters
    @Autowired
    private void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    @Autowired
    private void setUserRegistrationMapper(UserRegistrationMapper userRegistrationMapper) {
        this.userRegistrationMapper = userRegistrationMapper;
    }
    //endregion

    //region RoleDTO <--> RoleEntity
    public RoleEntity convert(RoleDTO role) {
        return roleMapper.convert(role);
    }

    public RoleDTO convert(RoleEntity role) {
        return roleMapper.convert(role);
    }
    //endregion

    public UserEntity convert(UserRegistrationDTO user) {
        return userRegistrationMapper.convert(user);
    }

}
