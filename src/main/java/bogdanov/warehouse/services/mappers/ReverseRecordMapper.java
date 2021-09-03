package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.ReverseRecordEntity;
import bogdanov.warehouse.dto.ReverseRecordDTO;
import org.springframework.stereotype.Component;

@Component
public class ReverseRecordMapper {

    ReverseRecordDTO convert(ReverseRecordEntity reverseRecord) {
        return new ReverseRecordDTO(
                reverseRecord.getId(),
                reverseRecord.getRevertedRecord().getId(),
                reverseRecord.getGeneratedRecord().getId(),
                reverseRecord.getTime(),
                reverseRecord.getUser().getUsername()
        );
    }

}
