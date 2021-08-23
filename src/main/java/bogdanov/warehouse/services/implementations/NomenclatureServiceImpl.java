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

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

//TODO
@Service
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final NomenclatureRepository nomenclatureRepository;
    private final Mapper mapper;

    @Override
    public NomenclatureDTO createNew(NomenclatureDTO nomenclature) {
        final NomenclatureException exception = new NomenclatureException();
        if (
                checkNameAvailability(nomenclature, exception)
                        && checkCodeAvailability(nomenclature, exception)
        ) {
            return mapper.convert(
                    nomenclatureRepository.save(
                            mapper.convert(nomenclature)
                    ));
        } else {
            throw exception;
        }
    }

    @Override
    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature) {
        final NomenclatureException exception = new NomenclatureException();

        nomenclature =
                nomenclature
                        .stream()
                        .filter(NomenclatureDTO::isNotEmpty)
                        .map(dto -> {
                            if (
                                    checkNameAvailability(dto, exception)
                                            && checkCodeAvailability(dto, exception)
                            ) {
                                return mapper.convert(nomenclatureRepository.save(mapper.convert(dto)));
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();

        if (exception.isEmpty()) {
            return nomenclature;
        } else {
            exception.acceptAll(nomenclature);
            throw exception;
        }
    }

    @Override
    public List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature) {
        return createNew(
                Arrays.asList(nomenclature));
    }

    @Override
    public NomenclatureDTO getById(long id) {
        try {
            return mapper.convert(nomenclatureRepository.getById(id));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Nomenclature with id : " + id + " not found"
            );
        }
    }

    @Override
    public NomenclatureDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new NomenclatureBlankNameException();
        }
        try {
            return mapper.convert(nomenclatureRepository.getByName(name));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Nomenclature with name : " + name + " not found"
            );
        }
    }

    @Override
    public NomenclatureDTO getByCode(String code) {
        if (Strings.isBlank(code)) {
            throw new NomenclatureBlankCodeException();
        }
        try {
            return mapper.convert(nomenclatureRepository.getByCode(code));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Nomenclature with code : " + code + " not found"
            );
        }
    }

    @Override
    public List<NomenclatureDTO> findAllByNameContaining(String partialName) {
        final List<NomenclatureEntity> entities;
        if (Strings.isBlank(partialName)) {
//            return Collections.EMPTY_LIST;
            throw new NomenclatureBlankNameException("Name is blank");
        }
        return nomenclatureRepository
                .findAllByNameContaining(partialName)
                .stream()
                .map(mapper::convert)
                .toList();
    }

    @Override
    public List<NomenclatureDTO> findAllByCodeContaining(String partialCode) {
        final List<NomenclatureEntity> entities;
        if (Strings.isBlank(partialCode)) {
            entities = nomenclatureRepository.findAllByCode(Strings.EMPTY);
        } else {
            entities = nomenclatureRepository.findAllByCodeContaining(partialCode);
        }
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO updateName(NomenclatureDTO nomenclature) {

        final NomenclatureException exception = new NomenclatureException();
        final NomenclatureEntity entity;

        if (
                (null != (entity = checkIdAndRetrieve(nomenclature, exception)))
                        & checkNameAvailability(nomenclature, exception)
                        && checkIdAndCodePair(nomenclature, entity, exception)
        ) {
            entity.setName(nomenclature.getName());
            return mapper.convert(nomenclatureRepository.save(entity));
        } else {
            throw exception;
        }

    }

    @Override
    public List<NomenclatureDTO> updateName(List<NomenclatureDTO> nomenclature) {
        final NomenclatureException exception = new NomenclatureException();

        nomenclature = nomenclature
                .stream()
                .filter(NomenclatureDTO::isNotEmpty)
                .map(dto -> {
                    final NomenclatureEntity entity;
                    if (
                            (null != (entity = checkIdAndRetrieve(dto, exception)))
                                    & checkCodeAvailability(dto, exception)
                                    && checkIdAndNamePair(dto, entity, exception)
                    ) {
                        entity.setCode(dto.getCode());
                        return mapper.convert(nomenclatureRepository.save(entity));
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (exception.isEmpty()) {
            return nomenclature;
        } else {
            exception.acceptAll(nomenclature);
            throw exception;
        }
    }

    @Override
    public List<NomenclatureDTO> updateName(NomenclatureDTO[] nomenclature) {
        return updateName(
                Arrays.asList(nomenclature));
    }

    @Override
    public NomenclatureDTO updateCode(NomenclatureDTO nomenclature) {

        final NomenclatureException exception = new NomenclatureException();
        final NomenclatureEntity entity;

        if (
                (null != (entity = checkIdAndRetrieve(nomenclature, exception)))
                        & checkCodeAvailability(nomenclature, exception)
                        && checkIdAndNamePair(nomenclature, entity, exception)
        ) {
            entity.setCode(nomenclature.getCode());
            return mapper.convert(nomenclatureRepository.save(entity));
        } else {
            throw exception;
        }

    }

    @Override
    public List<NomenclatureDTO> updateCode(List<NomenclatureDTO> nomenclature) {
        final NomenclatureException exception = new NomenclatureException();

        nomenclature =
                nomenclature
                        .stream()
                        .filter(NomenclatureDTO::isNotEmpty)
                        .map(dto -> {
                            final NomenclatureEntity entity;
                            if (
                                    (null != (entity = checkIdAndRetrieve(dto, exception)))
                                            & checkCodeAvailability(dto, exception)
                                            && checkIdAndNamePair(dto, entity, exception)
                            ) {
                                entity.setCode(dto.getCode());
                                return mapper.convert(nomenclatureRepository.save(entity));
                            } else {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();

        if (exception.isEmpty()) {
            return nomenclature;
        } else {
            exception.acceptAll(nomenclature);
            throw exception;
        }
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
        if (dto.getId() == null) {
            throw new NullIdException();
        }
        try {
            nomenclatureRepository.getById(dto.getId());
            return true;
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Nomenclature with id : " + dto.getId() + " not found");
        }
    }

    @Override
    public boolean checkId(NomenclatureDTO dto, NomenclatureException e) {
        try {
            return checkId(dto);
        } catch (ResourceNotFoundException | NullIdException ex) {
            e.add(dto, ex);
            return false;
        }
    }

    @Override
    public NomenclatureEntity checkIdAndRetrieve(NomenclatureDTO dto) {
        if (dto.getId() == null) {
            throw new NullIdException();
        }
        try {
            return nomenclatureRepository.getById(dto.getId());
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Nomenclature with id : " + dto.getId() + " not found");
        }
    }

    @Override
    public NomenclatureEntity checkIdAndRetrieve(NomenclatureDTO dto, NomenclatureException e) {
        try {
            return checkIdAndRetrieve(dto);
        } catch (ResourceNotFoundException | NullIdException ex) {
            e.add(dto, ex);
            return null;
        }
    }

    //TODO Check results
    @Override
    public boolean checkNameAvailability(NomenclatureDTO dto) {
        if (Strings.isBlank(dto.getName())) {
            throw new NomenclatureBlankNameException("Name is blank");
        }
        NomenclatureEntity entity = nomenclatureRepository.getByName(dto.getName());
        if (entity != null) {
            throw new NomenclatureAlreadyTakenNameException(
                    "Name : " + dto.getName() + " belongs to id : " + entity.getId()
            );
        }
        return true;
    }

    @Override
    public boolean checkNameAvailability(NomenclatureDTO dto, NomenclatureException e) {
        try {
            return checkNameAvailability(dto);
        } catch (NomenclatureAlreadyTakenNameException ex) {
            e.add(dto, ex);
            return false;
        }
    }

    @Override
    public boolean checkCodeAvailability(NomenclatureDTO dto) {
        if (Strings.isBlank(dto.getCode())) {
            return true;
        }
        NomenclatureEntity entity = nomenclatureRepository.getByCode(dto.getCode());
        if (entity != null) {
            throw new NomenclatureAlreadyTakenCodeException(
                    "code : " + dto.getCode() + " belongs to id : " + entity.getId()
            );
        }
        return true;
    }

    @Override
    public boolean checkCodeAvailability(NomenclatureDTO dto, NomenclatureException e) {
        try {
            return checkCodeAvailability(dto);
        } catch (NomenclatureAlreadyTakenCodeException ex) {
            e.add(dto, ex);
            return true;
        }
    }

    @Override
    public boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity) {
        if (entity.getName().equals(dto.getName())) {
            return true;
        } else {
            throw new NomenclatureWrongIdNamePairException(
                    "Id : Name (" + dto.getId() + " : " + dto.getName() + ") pair is incorrect\n"
                            + "Nomenclature with id : " + dto.getId() + " has name : " + entity.getName()
            );
        }
    }

    @Override
    public boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e) {
        try {
            return checkIdAndNamePair(dto, entity);
        } catch (NomenclatureWrongIdNamePairException ex) {
            e.add(dto, ex);
            return false;
        }
    }

    @Override
    public boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity) {
        boolean areBothBlank = Strings.isBlank(dto.getCode()) && Strings.isBlank(entity.getCode());
        if (areBothBlank || entity.getCode().equals(dto.getCode())) {
            return true;
        } else {
            throw new NomenclatureWrongIdCodePairException(
                    "Id : Code (" + dto.getId() + " : " + dto.getCode() + ") pair is incorrect\n"
                            + "Nomenclature with id : " + dto.getId() + " has code : " + entity.getName()
            );
        }
    }

    @Override
    public boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity, NomenclatureException e) {
        try {
            return checkIdAndCodePair(dto, entity);
        } catch (NomenclatureWrongIdCodePairException ex) {
            e.add(dto, ex);
            return false;
        }
    }

    @Override
    public boolean checkAmount(NomenclatureDTO dto) {
        if (dto.getAmount() == null || (dto.getAmount() < 0)) {
            throw new NomenclatureNotPositiveAmount();
        }
        return true;
    }

    @Override
    public boolean checkAmount(NomenclatureDTO dto, NomenclatureException e) {
        try {
            return checkAmount(dto);
        } catch (NomenclatureNotPositiveAmount ex) {
            e.add(dto, ex);
            return false;
        }
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

    //TODO Change to check operations and data(For tests)
    @Override
    public void deleteAll() {
        nomenclatureRepository.deleteAll();
    }
}
