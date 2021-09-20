package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.dto.RecordDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecordMapper {

    RecordEntity convert(RecordDTO record) {
        RecordEntity entity = new RecordEntity();
        entity.setAmount(record.getAmount());
        return entity;
    }

    RecordDTO convert(RecordEntity record) {
        RecordDTO dto = new RecordDTO();
        dto.setId(record.getId());
        dto.setNomenclatureId(record.getNomenclature().getId());
        dto.setType(record.getType().getName());
        dto.setAmount(record.getAmount());
        dto.setTime(record.getTime());
        dto.setUserId(record.getUser().getId());
        return dto;
    }

}
