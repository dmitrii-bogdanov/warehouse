package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class PositionMapper {

    PositionEntity convert(PositionDTO position) {
        if (Strings.isBlank(position.getName())) {
            throw new IllegalArgumentException(
                    ExceptionMessage.BLANK_ENTITY_NAME.setEntity(PositionEntity.class).getModifiedMessage());
        }
        return new PositionEntity(position.getId(), position.getName().toUpperCase(Locale.ROOT));
    }

    PositionDTO convert(PositionEntity position) {
        return new PositionDTO(position.getId(), position.getName());
    }

}
