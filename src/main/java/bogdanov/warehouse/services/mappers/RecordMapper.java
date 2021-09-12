package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import bogdanov.warehouse.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
        entity.setAmount(record.getAmount());
        return entity;
    }

    RecordDTO convert(RecordEntity record) {
        RecordOutputDTO dto = new RecordOutputDTO();
        dto.setId(record.getId());
        dto.setNomenclatureId(record.getNomenclature().getId());
        dto.setType(record.getType().getName());
        dto.setAmount(record.getAmount());
        dto.setTime(record.getTime());
        dto.setUsername(record.getUser().getUsername());
        return dto;
    }

    RecordEntity convert(RecordOutputDTO record) {
        RecordEntity entity = new RecordEntity();
        entity.setId(record.getId());
        entity.setNomenclature(nomenclatureRepository.getById(record.getNomenclatureId()));
        entity.setType(recordTypeMapper.convert(recordTypeService.getByName(record.getType())));
        entity.setAmount(record.getAmount());
        entity.setTime(record.getTime());
        return entity;
    }

}
