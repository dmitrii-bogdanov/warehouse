package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RecordEntity;
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

    private final NomenclatureService nomenclatureService;
    private final RecordTypeService recordTypeService;
    private final Mapper mapper;

    RecordEntity convert(RecordInputDTO record) {
        RecordEntity entity = new RecordEntity();
        entity.setId(record.getId());
        entity.setNomenclature(nomenclatureService.getEntityById(record.getNomenclatureId()));
        entity.setType(mapper.convert(recordTypeService.getByName(record.getType())));
        entity.setAmount(record.getAmount()
        );
        return entity;
    }

    RecordDTO convert(RecordEntity record) {
        RecordOutputDTO dto = new RecordOutputDTO();
        dto.setId(record.getId());
        dto.setNomenclature(mapper.convert(record.getNomenclature()));
        dto.setType(record.getType().getName());
        dto.setAmount(record.getAmount());
        dto.setTime(record.getTime());
        dto.setUser(mapper.convert(record.getUser()));
        return dto;
    }

}
