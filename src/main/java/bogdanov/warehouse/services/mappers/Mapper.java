package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.*;
import bogdanov.warehouse.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final RoleMapper roleMapper;
    private final UserAccountMapper userAccountMapper;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final NomenclatureMapper nomenclatureMapper;
    private final PositionMapper positionMapper;
    private final RecordMapper recordMapper;
    private final RecordTypeMapper recordTypeMapper;

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
        return userAccountMapper.convert(user);
    }

    public UserEntity convert(UserAccountDTO user) {
        return userAccountMapper.convert(user);
    }

    public UserAccountDTO convert(UserEntity user, Class<? extends UserAccountDTO> dto) {
        return userAccountMapper.convert(user);
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

    //region RecordDTO <--> RecordEntity
    public RecordEntity convert(RecordInputDTO record) {
        return recordMapper.convert(record);
    }

    public RecordDTO convert(RecordEntity record) {
        return recordMapper.convert(record);
    }

    public RecordEntity convert(RecordOutputDTO record) {
        return recordMapper.convert(record);
    }
    //endregion

    //region RecordTypeDTO <--> RecordTypeEntity
    public RecordTypeEntity convert(RecordTypeDTO recordType) {
        return recordTypeMapper.convert(recordType);
    }

    public RecordTypeDTO convert(RecordTypeEntity recordType) {
        return recordTypeMapper.convert(recordType);
    }
    //endregion

}
