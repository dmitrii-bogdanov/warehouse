package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import org.springframework.stereotype.Component;

//TODO
@Component
public class NomenclatureMapper {

    //TODO Change public to default visibility
    public NomenclatureDTO convert(NomenclatureEntity nomenclature) {
        return new NomenclatureDTO(
                nomenclature.getId(),
                nomenclature.getName(),
                nomenclature.getCode(),
                nomenclature.getAmount()
                );
    }

    //TODO Change public to default visibility
    public NomenclatureEntity convert(NomenclatureDTO nomenclature) {
        NomenclatureEntity nomenclatureEntity = new NomenclatureEntity();

        nomenclatureEntity.setName(nomenclature.getName());
        nomenclatureEntity.setCode(nomenclature.getCode());

        return nomenclatureEntity;
    }

}
