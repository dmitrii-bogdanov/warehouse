package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.NomenclatureException;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO
@Service
public interface NomenclatureService {

    NomenclatureDTO createNew(NomenclatureDTO nomenclature);

    List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature);

    List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature);

    NomenclatureDTO getById(Long id);

    NomenclatureEntity getEntityById(Long id);

    NomenclatureDTO getByName(String name);

    NomenclatureDTO getByCode(String code);

    List<NomenclatureDTO> findAllByNameContaining(String partialName);

    List<NomenclatureDTO> findAllByCodeContaining(String partialCode);

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

    boolean checkId(NomenclatureDTO dto, NomenclatureException e);

    NomenclatureEntity checkIdAndRetrieve(NomenclatureDTO dto);

    NomenclatureEntity checkIdAndRetrieve(NomenclatureDTO dto, NomenclatureException e);

    boolean checkNameAvailability(NomenclatureDTO dto);

    boolean checkNameAvailability(NomenclatureDTO dto, NomenclatureException e);


    boolean checkCodeAvailability(NomenclatureDTO dto);

    boolean checkCodeAvailability(NomenclatureDTO dto, NomenclatureException e);


    boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity);

    boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e);

//    List<NomenclatureDTO> checkIdAndName(List<NomenclatureDTO> nomenclature, NomenclatureException e);


    boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity);

    boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e);

//    List<NomenclatureDTO> checkIdAndCodePair(List<NomenclatureDTO> nomenclature, NomenclatureException e);


    boolean checkAmount(NomenclatureDTO dto);

    boolean checkAmount(NomenclatureDTO dto, NomenclatureException e);


    boolean checkAmountAvailability(NomenclatureDTO dto, NomenclatureEntity entity);

    boolean checkAmountAvailability(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e);

//    List<NomenclatureDTO> checkData(List<NomenclatureDTO nomenclature>);


}
