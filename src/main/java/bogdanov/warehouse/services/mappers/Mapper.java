package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.*;
import bogdanov.warehouse.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final UserAccountMapper userAccountMapper;
    private final UserMapper userMapper;
    private final PersonMapper personMapper;
    private final NomenclatureMapper nomenclatureMapper;
    private final RecordMapper recordMapper;
    private final ReverseRecordMapper reverseRecordMapper;

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

    //region UserDTO <-- UserEntity, UserDTO <- UserAccountDTO
    public UserDTO convert(UserEntity user) {
        return userMapper.convert(user);
    }

    public UserDTO convert(UserAccountDTO account, Class<? extends UserDTO> dto) {
        return userMapper.convert(account);
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

    //region RecordDTO <--> RecordEntity
    public RecordEntity convert(RecordDTO record) {
        return recordMapper.convert(record);
    }

    public RecordDTO convert(RecordEntity record) {
        return recordMapper.convert(record);
    }
    //endregion

    //region ReverseRecordDTO <-- ReverseRecordEntity
    public ReverseRecordDTO convert(ReverseRecordEntity reverseRecord) {
        return reverseRecordMapper.convert(reverseRecord);
    }
    //endregion

}
