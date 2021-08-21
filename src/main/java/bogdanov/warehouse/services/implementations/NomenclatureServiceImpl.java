package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.NomenclatureAlreadyTakenNameException;
import bogdanov.warehouse.exceptions.NomenclatureWrongIdCodePairException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.NomenclatureMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//TODO
@Service
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final NomenclatureRepository nomenclatureRepository;
    //TODO Change to Mapper
    private final NomenclatureMapper mapper;

    @Override
    public NomenclatureDTO createNew(NomenclatureDTO nomenclature) {
        boolean isNameAvailable = nomenclatureRepository.getByName(nomenclature.getName()) == null;
        boolean isCodeAvailable = nomenclatureRepository.getByCode(nomenclature.getCode()) == null;

        if (isNameAvailable && isCodeAvailable) {
            return mapper.convert(
                    nomenclatureRepository.save(
                            mapper.convert(nomenclature
                            )));
        } else {
            return nomenclature;
        }
    }

    @Override
    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature) {
        return nomenclature.stream().map(this::createNew).collect(Collectors.toList());
    }

    @Override
    public List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature) {
        return createNew(Arrays.asList(nomenclature));
    }

    @Override
    public NomenclatureDTO getById(long id) {
        return mapper.convert(nomenclatureRepository.getById(id));
    }

    @Override
    public NomenclatureDTO getByName(String name) {
        return mapper.convert(nomenclatureRepository.getByName(name));
    }

    @Override
    public NomenclatureDTO getByCode(String code) {
        return mapper.convert(nomenclatureRepository.getByCode(code));
    }

    @Override
    public NomenclatureDTO updateName(NomenclatureDTO nomenclature) {
        NomenclatureEntity nomenclatureEntity = nomenclatureRepository.getById(nomenclature.getId());
        if (nomenclatureEntity != null) {
            if (Strings.isBlank(nomenclature.getCode())
                    & Strings.isBlank(nomenclatureEntity.getCode())
                    || nomenclatureEntity.getCode().equals(nomenclature.getCode())) {
                if (nomenclatureRepository.getByName(nomenclature.getName()) == null) {
                    nomenclatureEntity.setName(nomenclature.getName());

                    return mapper.convert(nomenclatureRepository.save(nomenclatureEntity));

                } else {
                    throw new NomenclatureAlreadyTakenNameException("Name is already taken");
                }
            } else {
                throw new NomenclatureWrongIdCodePairException(
                        nomenclature.getId() + " " + nomenclature.getCode() +
                                "Incorrect Nomenclature Id Code Pair");
            }
        } else {
            throw new ResourceNotFoundException(nomenclature.getId() + " id; Nomenclature Resource Not Found");
        }
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
