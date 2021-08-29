package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.BlankNameException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    PositionEntity convert(PositionDTO position) {
        if (Strings.isBlank(position.getName())) {
            throw new BlankNameException(PositionEntity.class);
        }
        return new PositionEntity(position.getId(), position.getName());
    }

    PositionDTO convert(PositionEntity position) {
        return new PositionDTO(position.getId(), position.getName());
    }

}
