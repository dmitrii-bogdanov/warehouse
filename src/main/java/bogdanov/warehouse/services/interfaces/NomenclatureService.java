package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

//TODO
@Service
public interface NomenclatureService {

    List<NomenclatureDTO> createNew(Collection<NomenclatureDTO> nomenclature);

    NomenclatureDTO getById(Long id);

    NomenclatureEntity getEntityById(Long id);

    NomenclatureDTO getByName(String name);

    NomenclatureDTO getByCode(String code);

    List<NomenclatureDTO> update(List<NomenclatureDTO> nomenclature);

    List<NomenclatureDTO> getAll();

    List<NomenclatureDTO> getAllAvailable();

    NomenclatureDTO addAmount(NomenclatureDTO nomenclature);

    NomenclatureDTO subtractAmount(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> search(String name, String code, Long minAmount, Long maxAmount);

    NomenclatureDTO delete(Long id);

}
