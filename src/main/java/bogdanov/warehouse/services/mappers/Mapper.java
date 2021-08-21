package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final RoleMapper roleMapper;
    private final UserRegistrationMapper userRegistrationMapper;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;

    //region RoleDTO <--> RoleEntity
    public RoleEntity convert(RoleDTO role) {
        return roleMapper.convert(role);
    }

    public RoleDTO convert(RoleEntity role) {
        return roleMapper.convert(role);
    }
    //endregion

    //region UserRegistrationDTO, UserRegistrationInfoDTO <--> UserEntity
    public UserEntity convert(UserAccountWithPasswordDTO user) {
        return userRegistrationMapper.convert(user);
    }

    public UserAccountDTO convert(UserEntity user, boolean isAdminConsole) {
        return isAdminConsole ? userRegistrationMapper.convert(user) : null;
    }
    //endregion

    //region UserDTO <-- UserEntity
    public UserDTO convert(UserEntity user) {
        return userMapper.convert(user);
    }
    //endregion

    //region PersonDTO <--> PersonEntity
    public PersonEntity convert(PersonDTO person) {
        return personMapper.convert(person);
    }

    public PersonDTO convert(PersonEntity person) {
        return personMapper.convert(person);
    }
    //endregion

}
