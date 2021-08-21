package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import org.springframework.stereotype.Component;

@Component
public class NomenclatureMapper {

    NomenclatureDTO convert(NomenclatureEntity nomenclature) {
        return new NomenclatureDTO(
                nomenclature.getId(),
                nomenclature.getName(),
                nomenclature.getCode(),
                nomenclature.getAmount()
                );
    }

}
