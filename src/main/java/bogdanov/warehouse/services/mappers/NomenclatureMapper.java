package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Locale;

//TODO
@Component
@RequiredArgsConstructor
public class NomenclatureMapper {

    private final ObjectMapper objectMapper;

    private String toUpperCase(String str) {
        if (Strings.isBlank(str)) {
            str = null;
        } else {
            str = str.toUpperCase(Locale.ROOT);
        }
        return str;
    }

    NomenclatureDTO convert(NomenclatureEntity nomenclature) {
        return objectMapper.convertValue(nomenclature, NomenclatureDTO.class);
    }

    NomenclatureEntity convert(NomenclatureDTO nomenclature) {
        return new NomenclatureEntity(
                null,
                toUpperCase(nomenclature.getName()),
                toUpperCase(nomenclature.getCode()),
                null);
    }

}
