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
        return nomenclature != null

                ? new NomenclatureDTO(
                nomenclature.getId(),
                nomenclature.getName(),
                nomenclature.getCode(),
                nomenclature.getAmount());
    }

    NomenclatureEntity convert(NomenclatureDTO nomenclature) {
        return new NomenclatureEntity(
                null,
                nomenclature.getName(),
                nomenclature.getCode(),
                null);
    }

}
