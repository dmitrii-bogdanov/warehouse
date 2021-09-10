package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO
@Service
public interface NomenclatureService {

    NomenclatureDTO createNew(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature);

    NomenclatureDTO getById(Long id);

    NomenclatureEntity getEntityById(Long id);

    NomenclatureDTO getByName(String name);

    NomenclatureDTO getByCode(String code);

    List<NomenclatureDTO> findAllByNameContaining(String partialName);

    List<NomenclatureDTO> findAllByCodeContaining(String partialCode);

    NomenclatureDTO update(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> update(List<NomenclatureDTO> nomenclature);

    NomenclatureDTO updateName(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> updateName(List<NomenclatureDTO> nomenclature);

    List<NomenclatureDTO> updateName(NomenclatureDTO[] nomenclature);

    NomenclatureDTO updateCode(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> updateCode(List<NomenclatureDTO> nomenclature);

    List<NomenclatureDTO> updateCode(NomenclatureDTO[] nomenclature);

    List<NomenclatureDTO> getAll();

    List<NomenclatureDTO> getAllAvailable();

    NomenclatureDTO addAmount(NomenclatureDTO nomenclature);

    NomenclatureDTO updateAmount(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> addAmount(List<NomenclatureDTO> nomenclature);

    List<NomenclatureDTO> addAmount(NomenclatureDTO[] nomenclature);

    NomenclatureDTO subtractAmount(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> subtractAmount(List<NomenclatureDTO> nomenclature);

    List<NomenclatureDTO> subtractAmount(NomenclatureDTO[] nomenclature);

    boolean checkId(NomenclatureDTO dto);

    NomenclatureEntity checkIdAndRetrieve(NomenclatureDTO dto);

    boolean checkNameAvailability(NomenclatureDTO dto);


    boolean checkCodeAvailability(NomenclatureDTO dto);


    boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity);




    boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity);




    boolean checkAmount(NomenclatureDTO dto);



    boolean checkAmountAvailability(NomenclatureDTO dto, NomenclatureEntity entity);


    List<NomenclatureDTO> findAllByNameContainingAndCodeContaining(String name, String code);


    NomenclatureDTO delete(Long id);

}
