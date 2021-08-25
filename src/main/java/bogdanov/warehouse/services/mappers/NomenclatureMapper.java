package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

//TODO
@Component
public class NomenclatureMapper {

    NomenclatureDTO convert(NomenclatureEntity nomenclature) {
        return nomenclature != null

                ? new NomenclatureDTO(
                nomenclature.getId(),
                nomenclature.getName(),
                nomenclature.getCode(),
                nomenclature.getAmount())

                : new NomenclatureDTO();
    }

    NomenclatureEntity convert(NomenclatureDTO nomenclature) {
        return new NomenclatureEntity(
                null,
                nomenclature.getName(),
                nomenclature.getCode(),
                null);
    }

}
