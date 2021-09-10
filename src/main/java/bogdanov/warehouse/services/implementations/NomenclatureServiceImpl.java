package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Primary
@Service
@Qualifier("withoutInternalChecks")
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final NomenclatureRepository nomenclatureRepository;
    private final RecordRepository recordRepository;
    private final Mapper mapper;

    private static final String DATA_INTEGRITY_EXCEPTION_SUBSTRING = "ON PUBLIC.NOMENCLATURE(";
    private static final String NOMENCLATURE = "Nomenclature";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CODE = "code";

    private boolean isCodeNotReserved(NomenclatureDTO dto) {
        if ("NULL".equalsIgnoreCase(dto.getCode())) {
            throw new ArgumentException(ExceptionType.RESERVED_VALUE
                    .setFieldName(CODE).setFieldValue(dto.getCode().toUpperCase(Locale.ROOT)));
        }
        return true;
    }

    @Override
    public NomenclatureDTO createNew(NomenclatureDTO nomenclature) {
        try {
            isCodeNotReserved(nomenclature);
            return mapper.convert(nomenclatureRepository.save(mapper.convert(nomenclature)));
        } catch (DataIntegrityViolationException e) {
            throw explainException(e);
        }
    }

    @Override
    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature) {
        try {
            return nomenclatureRepository
                    .saveAll(nomenclature.stream().filter(this::isCodeNotReserved).map(mapper::convert).toList())
                    .stream().map(mapper::convert).toList();
        } catch (DataIntegrityViolationException e) {
            throw explainException(e);
        }
    }

    private RuntimeException explainException(DataIntegrityViolationException e) {
        StringBuilder sb = new StringBuilder(e.getMessage());
        int index;
        if ((index = sb.indexOf(DATA_INTEGRITY_EXCEPTION_SUBSTRING)) > -1) {
            ExceptionType type = null;
            sb.delete(0, index + DATA_INTEGRITY_EXCEPTION_SUBSTRING.length());
            long id = Long.parseLong(sb.substring(13, sb.indexOf("\"")));
            Optional<NomenclatureEntity> entity = nomenclatureRepository.findById(id);
            if (entity.isPresent()) {
                String field = sb.substring(0, 4);
                if (CODE.equalsIgnoreCase(field)) {
                    type = ExceptionType.ALREADY_RECORDED_NAME_OR_CODE
                            .setFieldName(CODE).setFieldValue(entity.get().getCode()).setId(id);
                }
                if (NAME.equalsIgnoreCase(field)) {
                    type = ExceptionType.ALREADY_RECORDED_NAME_OR_CODE
                            .setFieldName(NAME).setFieldValue(entity.get().getName()).setId(id);
                }
            } else {
                type = ExceptionType.LIST_CONTAINS_REPEATING_VALUES
                        .setFieldName(sb.substring(0, 4).toLowerCase(Locale.ROOT));
            }
            return new ArgumentException(type);
        } else {
            return e;
        }
    }

    @Override
    public NomenclatureDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public NomenclatureEntity getEntityById(Long id) {
        Optional<NomenclatureEntity> entity = nomenclatureRepository.findById(id);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new ResourceNotFoundException(NOMENCLATURE, ID, id);
        }
    }

    @Override
    public NomenclatureDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(ExceptionType.BLANK_NAME.getMessage());
        }
        Optional<NomenclatureEntity> entity = nomenclatureRepository.findByNameIgnoreCase(name);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
        } else {
            throw new ResourceNotFoundException(NOMENCLATURE, NAME, name);
        }
    }

    @Override
    public NomenclatureDTO getByCode(String code) {
        if (Strings.isBlank(code)) {
            throw new ArgumentException(ExceptionType.BLANK_CODE
                    .addComment("If system allows to use blank codes, try ?search?code=null"));
        }
        return mapper.convert(
                nomenclatureRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException(NOMENCLATURE, CODE, code)));
    }

    @Override
    public List<NomenclatureDTO> findAllByNameContaining(String partialName) {
        if (Strings.isBlank(partialName)) {
            throw new IllegalArgumentException(ExceptionType.BLANK_NAME.getMessage());
        }
        return nomenclatureRepository.findAllByNameContainingIgnoreCase(partialName)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<NomenclatureDTO> findAllByCodeContaining(String partialCode) {
        List<NomenclatureEntity> entities;
        if (Strings.isBlank(partialCode)) {
            entities = nomenclatureRepository.findAllByCodeIsNull();
        } else {
            partialCode = partialCode.toUpperCase(Locale.ROOT);
            entities = nomenclatureRepository.findAllByCodeContainingIgnoreCase(partialCode);
        }
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO updateName(NomenclatureDTO nomenclature) {
        try {
            NomenclatureEntity entity = getEntityById(nomenclature.getId());
            entity.setName(nomenclature.getName());
            return mapper.convert(nomenclatureRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    ExceptionType.NAME_OR_CODE_IS_ALREADY_REGISTERED
                            .setFieldName(NAME)
                            .setFieldValue(nomenclature.getName())
                            .getModifiedMessage());
        }
    }

    @Override
    public List<NomenclatureDTO> updateName(List<NomenclatureDTO> nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NomenclatureDTO> updateName(NomenclatureDTO[] nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NomenclatureDTO updateCode(NomenclatureDTO nomenclature) {
        try {
            NomenclatureEntity entity = getEntityById(nomenclature.getId());
            entity.setCode(nomenclature.getCode());
            return mapper.convert(nomenclatureRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    ExceptionType.NAME_OR_CODE_IS_ALREADY_REGISTERED
                            .setFieldName(CODE)
                            .setFieldValue(nomenclature.getCode())
                            .getModifiedMessage());
        }
    }

    @Override
    public List<NomenclatureDTO> updateCode(List<NomenclatureDTO> nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NomenclatureDTO> updateCode(NomenclatureDTO[] nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NomenclatureDTO> getAll() {
        return nomenclatureRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<NomenclatureDTO> getAllAvailable() {
        return nomenclatureRepository.findAllByAmountGreaterThan(0).stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO addAmount(NomenclatureDTO nomenclature) {
        NomenclatureEntity entity = getEntityById(nomenclature.getId());
        entity.add(nomenclature.getAmount());
        return mapper.convert(nomenclatureRepository.save(entity));
    }

    @Override
    public NomenclatureDTO updateAmount(NomenclatureDTO nomenclature) {
        NomenclatureEntity entity = getEntityById(nomenclature.getId());
        entity.setAmount(nomenclature.getAmount());
        return mapper.convert(nomenclatureRepository.save(entity));
    }

    @Override
    public List<NomenclatureDTO> addAmount(List<NomenclatureDTO> nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NomenclatureDTO> addAmount(NomenclatureDTO[] nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NomenclatureDTO subtractAmount(NomenclatureDTO nomenclature) {
        NomenclatureEntity entity = getEntityById(nomenclature.getId());
        checkAmountAvailability(nomenclature, entity);
        entity.take(nomenclature.getAmount());
        return mapper.convert(nomenclatureRepository.save(entity));

    }

    @Override
    public List<NomenclatureDTO> subtractAmount(List<NomenclatureDTO> nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<NomenclatureDTO> subtractAmount(NomenclatureDTO[] nomenclature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkId(NomenclatureDTO dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NomenclatureEntity checkIdAndRetrieve(NomenclatureDTO dto) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean checkNameAvailability(NomenclatureDTO dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkCodeAvailability(NomenclatureDTO dto) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean checkIdAndNamePair(NomenclatureDTO dto, NomenclatureEntity entity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkIdAndCodePair(NomenclatureDTO dto, NomenclatureEntity entity) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean checkAmount(NomenclatureDTO dto) {
        if (dto.getId() == null || dto.getId() < 0) {
            throw new IllegalArgumentException(ExceptionType.NOT_POSITIVE_AMOUNT.getMessage());
        }
        return true;
    }


    @Override
    public boolean checkAmountAvailability(NomenclatureDTO dto, NomenclatureEntity entity) {
        if (checkAmount(dto) && (entity.getAmount() >= dto.getAmount())) {
            return true;
        } else {
            throw new IllegalArgumentException(
                    ExceptionType.NOT_ENOUGH_AMOUNT.setId(entity.getId()).getModifiedMessage());
        }
    }

    @Override
    public NomenclatureDTO update(NomenclatureDTO nomenclature) {
        NomenclatureEntity entity = checkForUpdateAndRetrieveEntity(nomenclature);
        entity.setName(nomenclature.getName());
        entity.setName(nomenclature.getName());
        entity = nomenclatureRepository.save(entity);
        return mapper.convert(entity);
    }

    @Override
    public List<NomenclatureDTO> update(List<NomenclatureDTO> nomenclature) {
        return nomenclatureRepository.saveAll(
                nomenclature
                        .stream()
                        .map(n -> {
                            NomenclatureEntity entity = checkForUpdateAndRetrieveEntity(n);
                            entity.setName(n.getName());
                            entity.setCode(n.getCode());
                            return entity;
                        })
                        .toList()
        )
                .stream()
                .map(mapper::convert)
                .toList();
    }

    //TODO check exceptions without this method
    private NomenclatureEntity checkForUpdateAndRetrieveEntity(NomenclatureDTO dto) {
        if (Strings.isBlank(dto.getName())) {
            throw new IllegalArgumentException(ExceptionType.BLANK_NAME.getMessage());
        }
        return nomenclatureRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(NOMENCLATURE, ID, dto.getId()));
    }

    @Override
    public List<NomenclatureDTO> findAllByNameContainingAndCodeContaining(String name, String code) {
        List<NomenclatureEntity> entities = null;
        boolean isNameBlank = Strings.isBlank(name);
        boolean isCodeBlank = Strings.isBlank(code);

        if (isNameBlank && isCodeBlank) {
            throw new IllegalArgumentException(ExceptionType.BLANK_NAME_AND_CODE.getMessage());
        }
        if (isCodeBlank) {
            entities = nomenclatureRepository.findAllByNameContainingIgnoreCase(name);
        }
        boolean isLookingForNullCode = "NULL".equalsIgnoreCase(code);
        if (isNameBlank) {
            entities = isLookingForNullCode
                    ? nomenclatureRepository.findAllByCodeIsNull()
                    : nomenclatureRepository.findAllByCodeContainingIgnoreCase(code);
        }
        if (!isNameBlank && !isCodeBlank) {
            entities = isLookingForNullCode
                    ? nomenclatureRepository.findAllByNameContainingIgnoreCaseAndCodeIsNull(name)
                    : nomenclatureRepository.findAllByNameContainingIgnoreCaseAndCodeContainingIgnoreCase(name, code);
        }

        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO delete(Long id) {
        NomenclatureEntity entity = getEntityById(id);
        if (recordRepository.existsByNomenclature_Id(id)) {
            throw new ProhibitedRemovingException(ExceptionType.NOMENCLATURE_HAS_RECORDS.setId(id));
        }
        if (entity.getAmount() <= 0) {
            throw new ProhibitedRemovingException(ExceptionType.NOMENCLATURE_AMOUNT_IS_POSITIVE.setId(id));
        }
        nomenclatureRepository.delete(entity);
        return mapper.convert(entity);
    }
}
