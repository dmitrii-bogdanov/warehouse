package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.*;
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
    private final NomenclatureMapper nomenclatureMapper;
    private final PositionMapper positionMapper;

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

    //region NomenclatureDTO <--> NomenclatureEntity
    public NomenclatureDTO convert(NomenclatureEntity nomenclature) {
        return nomenclatureMapper.convert(nomenclature);
    }

    public NomenclatureEntity convert(NomenclatureDTO nomenclature) {
        return nomenclatureMapper.convert(nomenclature);
    }
    //endregion

    //region PositionDTO <--> PositionEntity
    public PositionEntity convert(PositionDTO position) {
        return positionMapper.convert(position);
    }

    public PositionDTO convert(PositionEntity position) {
        return positionMapper.convert(position);
    }
    //endregion

}
