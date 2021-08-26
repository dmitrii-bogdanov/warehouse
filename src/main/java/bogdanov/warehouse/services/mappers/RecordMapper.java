package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import bogdanov.warehouse.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecordMapper {

    private final NomenclatureRepository nomenclatureRepository;
    private final RecordTypeService recordTypeService;
    private final NomenclatureMapper nomenclatureMapper;
    private final RecordTypeMapper recordTypeMapper;
    private final UserMapper userMapper;

    RecordEntity convert(RecordInputDTO record) {
        RecordEntity entity = new RecordEntity();
        entity.setId(record.getId());
        entity.setNomenclature(nomenclatureRepository.getById(record.getNomenclatureId()));
        entity.setType(recordTypeMapper.convert(recordTypeService.getByName(record.getType())));
        entity.setAmount(record.getAmount()
        );
        return entity;
    }

    RecordDTO convert(RecordEntity record) {
        RecordOutputDTO dto = new RecordOutputDTO();
        dto.setId(record.getId());
        dto.setNomenclature(nomenclatureMapper.convert(record.getNomenclature()));
        dto.setType(record.getType().getName());
        dto.setAmount(record.getAmount());
        dto.setTime(record.getTime());
        dto.setUser(userMapper.convert(record.getUser()));
        return dto;
    }

}