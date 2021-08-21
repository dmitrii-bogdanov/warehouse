package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO
@Service
public interface NomenclatureService {

    public NomenclatureDTO createNew(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature);

    public List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature);

    public NomenclatureDTO getById(long id);

    public NomenclatureDTO getByName(String name);

    public NomenclatureDTO getByCode(String name);

    public NomenclatureDTO updateName(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> updateName(List<NomenclatureDTO> nomenclature);

    public List<NomenclatureDTO> updateName(NomenclatureDTO[] nomenclature);

    public NomenclatureDTO updateCode(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> updateCode(List<NomenclatureDTO> nomenclature);

    public List<NomenclatureDTO> updateCode(NomenclatureDTO[] nomenclature);

    public List<NomenclatureDTO> getAll();

    public List<NomenclatureDTO> getAllAvailable();

    public NomenclatureDTO addAmount(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> addAmount(List<NomenclatureDTO> nomenclature);

    public List<NomenclatureDTO> addAmount(NomenclatureDTO[] nomenclature);

    public NomenclatureDTO subtractAmount(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> subtractAmount(List<NomenclatureDTO> nomenclature);

    public List<NomenclatureDTO> subtractAmount(NomenclatureDTO[] nomenclature);

    public NomenclatureDTO checkIdAndName(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> checkIdAndName(List<NomenclatureDTO> nomenclature);

    public NomenclatureDTO checkIdAndCode(NomenclatureDTO nomenclature);

    public List<NomenclatureDTO> checkIdAndCode(List<NomenclatureDTO> nomenclature);

    public NomenclatureDTO checkData(NomenclatureDTO nomenclature, boolean checkAmount);

    public List<NomenclatureDTO> checkData(List<NomenclatureDTO> nomenclature, boolean checkAmount);

}
