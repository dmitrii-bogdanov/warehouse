package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.dto.RecordTypeDTO;
import org.springframework.stereotype.Component;

@Component
public class RecordTypeMapper {

    RecordTypeEntity convert(RecordTypeDTO recordType) {
        return new RecordTypeEntity(recordType.getId(), recordType.getName());
    }

    RecordTypeDTO convert(RecordTypeEntity recordType) {
        return new RecordTypeDTO(recordType.getId(), recordType.getName());
    }

}
