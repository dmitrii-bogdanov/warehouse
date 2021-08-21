package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.NomenclatureMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO
@Service
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final NomenclatureRepository nomenclatureRepository;
    //TODO Change to Mapper
    private final NomenclatureMapper mapper;

    @Override
    public NomenclatureDTO createNew(NomenclatureDTO nomenclature) {
        boolean nameIsAvailable = nomenclatureRepository.getByName(nomenclature.getName()) == null;
        boolean codeIsAvailable = nomenclatureRepository.getByCode(nomenclature.getCode()) == null;

        if ((name))
    }

    @Override
    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature) {
        return
    }

    @Override
    public List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature) {
        return null;
    }

    @Override
    public NomenclatureDTO getById(long id) {
        return null;
    }

    @Override
    public NomenclatureDTO getByName(String name) {
        return null;
    }

    @Override
    public NomenclatureDTO getByCode(String name) {
        return null;
    }

    @Override
    public NomenclatureDTO updateName(NomenclatureDTO nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> updateName(List<NomenclatureDTO> nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> updateName(NomenclatureDTO[] nomenclature) {
        return null;
    }

    @Override
    public NomenclatureDTO updateCode(NomenclatureDTO nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> updateCode(List<NomenclatureDTO> nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> updateCode(NomenclatureDTO[] nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> getAll() {
        return null;
    }

    @Override
    public List<NomenclatureDTO> getAllAvailable() {
        return null;
    }

    @Override
    public NomenclatureDTO addAmount(NomenclatureDTO nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> addAmount(List<NomenclatureDTO> nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> addAmount(NomenclatureDTO[] nomenclature) {
        return null;
    }

    @Override
    public NomenclatureDTO subtractAmount(NomenclatureDTO nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> subtractAmount(List<NomenclatureDTO> nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> subtractAmount(NomenclatureDTO[] nomenclature) {
        return null;
    }

    @Override
    public NomenclatureDTO checkIdAndName(NomenclatureDTO nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> checkIdAndName(List<NomenclatureDTO> nomenclature) {
        return null;
    }

    @Override
    public NomenclatureDTO checkIdAndCode(NomenclatureDTO nomenclature) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> checkIdAndCode(List<NomenclatureDTO> nomenclature) {
        return null;
    }

    @Override
    public NomenclatureDTO checkData(NomenclatureDTO nomenclature, boolean checkAmount) {
        return null;
    }

    @Override
    public List<NomenclatureDTO> checkData(List<NomenclatureDTO> nomenclature, boolean checkAmount) {
        return null;
    }
}
