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
        boolean isNameAvailable = nomenclatureRepository.getByName(nomenclature.getName()) == null;
        boolean isCodeAvailable = nomenclatureRepository.getByCode(nomenclature.getCode()) == null;

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

    @Override
    public NomenclatureDTO updateCode(NomenclatureDTO nomenclature) {
        if (nomenclature.getId() != null) {
            NomenclatureEntity nomenclatureEntity = nomenclatureRepository.getById(nomenclature.getId());
            boolean isBothBlank = Strings.isBlank(nomenclature.getName())
                    && Strings.isBlank(nomenclatureEntity.getName());
            if (isBothBlank || nomenclatureEntity.getName().equals(nomenclature.getName())) {
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
