package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.Mapper;
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
    private final Mapper mapper;

    @Override
    public NomenclatureDTO createNew(NomenclatureDTO nomenclature) {
        //TODO Check if getByName can return NULL
        boolean isNameAvailable = nomenclatureRepository.getByName(nomenclature.getName()) == null;
        boolean isCodeAvailable = nomenclatureRepository.getByCode(nomenclature.getCode()) == null;

        if (Strings.isNotBlank(nomenclature.getName())) {
            if (isNameAvailable) {
                if (isCodeAvailable) {
                    return mapper.convert(
                            nomenclatureRepository.save(
                                    mapper.convert(nomenclature
                                    )));
                } else {
                    throw new NomenclatureAlreadyTakenCodeException(nomenclature.toString());
                }
            } else {
                throw new NomenclatureAlreadyTakenNameException(nomenclature.toString());
            }
        } else {
            throw new NomenclatureBlankNameException(nomenclature.toString());
        }
    }

    @Override
    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature) {
        return nomenclature
                .stream()
                .map(this::createNew)
                .collect(Collectors.toList());
    }

    @Override
    public List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature) {
        return createNew(
                Arrays.asList(nomenclature));
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
        if (nomenclature.getId() != null) {
            NomenclatureEntity nomenclatureEntity = nomenclatureRepository.getById(nomenclature.getId());
            boolean isBothBlank = Strings.isBlank(nomenclature.getCode())
                    && Strings.isBlank(nomenclatureEntity.getCode());
            if (isBothBlank || nomenclatureEntity.getCode().equals(nomenclature.getCode())) {
                //TODO CHECK if getByName can return NULL
                boolean isNameAvailable = nomenclatureRepository.getByName(nomenclature.getName()) == null;
                if (isNameAvailable) {
                    nomenclatureEntity.setName(nomenclature.getName());

                    return mapper.convert(nomenclatureRepository.save(nomenclatureEntity));

                } else {
                    throw new NomenclatureAlreadyTakenNameException(nomenclature.toString());
                }
            } else {
                throw new NomenclatureWrongIdCodePairException(nomenclature.toString());
            }
        } else {
            throw new NullIdException(nomenclature.toString());
        }
    }

    @Override
    public List<NomenclatureDTO> updateName(List<NomenclatureDTO> nomenclature) {
        return nomenclature
                .stream()
                .map(this::updateName)
                .collect(Collectors.toList());
    }

    @Override
    public List<NomenclatureDTO> updateName(NomenclatureDTO[] nomenclature) {
        return updateName(
                Arrays.asList(nomenclature));
    }

    //TODO Check at morning
    @Override
    public NomenclatureDTO updateCode(NomenclatureDTO nomenclature) {
        if (nomenclature.getId() != null) {
            NomenclatureEntity nomenclatureEntity = nomenclatureRepository.getById(nomenclature.getId());
            if (nomenclatureEntity.getName().equals(nomenclature.getName())) {
                boolean isCodeAvailable = nomenclatureRepository.getByCode(nomenclature.getCode()) == null;
                if (isCodeAvailable) {
                    nomenclatureEntity.setCode(nomenclature.getCode());

                    return mapper.convert(nomenclatureRepository.save(nomenclatureEntity));

                } else {
                    throw new NomenclatureAlreadyTakenCodeException(nomenclature.toString());
                }
            } else {
                throw new NomenclatureWrongIdNamePairException(nomenclature.toString());
            }
        } else {
            throw new NullIdException(nomenclature.toString());
        }
    }

    @Override
    public List<NomenclatureDTO> updateCode(List<NomenclatureDTO> nomenclature) {
        return nomenclature
                .stream()
                .map(this::updateCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<NomenclatureDTO> updateCode(NomenclatureDTO[] nomenclature) {
        return updateCode(
                Arrays.asList(nomenclature));
    }

    @Override
    public List<NomenclatureDTO> getAll() {
        return nomenclatureRepository.findAll()
                .stream()
                .map(mapper::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<NomenclatureDTO> getAllAvailable() {
        return nomenclatureRepository.findAllByAmountGreaterThan(0)
                .stream()
                .map(mapper::convert)
                .collect(Collectors.toList());
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
    public boolean checkId(NomenclatureDTO dto) {
        return false;
    }

    @Override
    public boolean checkId(NomenclatureDTO dto, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkNameAvailability(NomenclatureDTO dto) {
        return false;
    }

    @Override
    public boolean checkNameAvailability(NomenclatureDTO dto, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkCodeAvailability(NomenclatureDTO dto) {
        return false;
    }

    @Override
    public boolean checkCodeAvailability(NomenclatureDTO dto, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity) {
        return false;
    }

    @Override
    public boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity) {
        return false;
    }

    @Override
    public boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkAmount(NomenclatureDTO dto) {
        return false;
    }

    @Override
    public boolean checkAmount(NomenclatureDTO dto, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkAmountAvailability(NomenclatureDTO dto) {
        return false;
    }

    @Override
    public boolean checkAmountAvailability(NomenclatureDTO dto, NomenclatureException e) {
        return false;
    }

    @Override
    public boolean checkData(NomenclatureDTO dto) {
        return false;
    }

    @Override
    public boolean checkData(NomenclatureDTO dto, NomenclatureException e) {
        return false;
    }
}
