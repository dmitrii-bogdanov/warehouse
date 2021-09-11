package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//TODO
@Component
@RequiredArgsConstructor
public class NomenclatureMapper {

    private final ObjectMapper objectMapper;

    NomenclatureDTO convert(NomenclatureEntity nomenclature) {
        return objectMapper.convertValue(nomenclature, NomenclatureDTO.class);
    }

    NomenclatureEntity convert(NomenclatureDTO nomenclature) {
        return new NomenclatureEntity(
                null,
                nomenclature.getName(),
                nomenclature.getCode(),
                null);
    }

}
