package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Primary
@Service
@Qualifier("withoutInternalChecks")
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final NomenclatureRepository nomenclatureRepository;
    private final RecordRepository recordRepository;
    private final Mapper mapper;
    private static final String DATA_INTEGRITY_EXCEPTION_SUBSTRING = "ON PUBLIC.NOMENCLATURE(";

    @Override
    public NomenclatureDTO createNew(NomenclatureDTO nomenclature) {
        try {
            return mapper.convert(nomenclatureRepository.save(mapper.convert(nomenclature)));
        } catch (DataIntegrityViolationException e) {
            throw explainException(e);
        }
    }

    //TODO change thrown exception
    @Override
    public List<NomenclatureDTO> createNew(List<NomenclatureDTO> nomenclature) {
        try {
            return nomenclatureRepository.saveAll(nomenclature.stream().map(mapper::convert).toList())
                    .stream().map(mapper::convert).toList();
        } catch (DataIntegrityViolationException e) {
            throw explainException(e);
        }
    }

    private RuntimeException explainException(DataIntegrityViolationException e) {
        StringBuilder sb = new StringBuilder(e.getMessage());
        int index;
        if ((index = sb.indexOf(DATA_INTEGRITY_EXCEPTION_SUBSTRING)) > -1) {
            String message = null;
            sb.delete(0, index + DATA_INTEGRITY_EXCEPTION_SUBSTRING.length());
            long id = Long.parseLong(sb.substring(13, sb.indexOf("\"")));
            Optional<NomenclatureEntity> entity = nomenclatureRepository.findById(id);
            if (entity.isPresent()) {
                switch (sb.substring(0, 4)) {
                    case "CODE" -> message = ExceptionMessage.ALREADY_RECORDED_NAME_OR_CODE
                            .setFieldName("Code").setFieldValue(entity.get().getCode()).setId(id).getModifiedMessage();
                    case "NAME" -> message = ExceptionMessage.ALREADY_RECORDED_NAME_OR_CODE
                            .setFieldName("Name").setFieldValue(entity.get().getName()).setId(id).getModifiedMessage();
                }
            } else {
                message = "Sent list contains repeating " + sb.substring(0, 4).toLowerCase(Locale.ROOT) + "s";
            }
            return new IllegalArgumentException(message);
        } else {
            return e;
        }
    }

    @Override
    public List<NomenclatureDTO> createNew(NomenclatureDTO[] nomenclature) {
        return createNew(Arrays.asList(nomenclature));
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
            throw new ResourceNotFoundException("Nomenclature", "id", id);
        }
    }

    @Override
    public NomenclatureDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_NAME.getMessage());
        }
        Optional<NomenclatureEntity> entity = nomenclatureRepository.findByName(name);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
        } else {
            throw new ResourceNotFoundException("Nomenclature", "name", name);
        }
    }

    @Override
    public NomenclatureDTO getByCode(String code) {
        if (Strings.isBlank(code)) {
            throw new IllegalArgumentException("Code is missing. If system allows to use blank codes, try ?search?code ");
        }
        Optional<NomenclatureEntity> entity = nomenclatureRepository.findByCode(code);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
        } else {
            throw new ResourceNotFoundException("Nomenclature", "code", code);
        }
    }

    @Override
    public List<NomenclatureDTO> findAllByNameContaining(String partialName) {
        if (Strings.isBlank(partialName)) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_NAME.getMessage());
        }
        partialName = partialName.toUpperCase(Locale.ROOT);
        return nomenclatureRepository.findAllByNameContaining(partialName).stream().map(mapper::convert).toList();
    }

    @Override
    public List<NomenclatureDTO> findAllByCodeContaining(String partialCode) {
        List<NomenclatureEntity> entities;
        if (Strings.isBlank(partialCode)) {
            entities = nomenclatureRepository.findAllByCode(null);
        } else {
            partialCode = partialCode.toUpperCase(Locale.ROOT);
            entities = nomenclatureRepository.findAllByCodeContaining(partialCode);
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
                    ExceptionMessage.NAME_OR_CODE_IS_ALREADY_REGISTERED
                            .setFieldName("Name")
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
                    ExceptionMessage.NAME_OR_CODE_IS_ALREADY_REGISTERED
                            .setFieldName("Code")
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
            throw new IllegalArgumentException(ExceptionMessage.NOT_POSITIVE_AMOUNT.getMessage());
        }
        return true;
    }


    @Override
    public boolean checkAmountAvailability(NomenclatureDTO dto, NomenclatureEntity entity) {
        if (checkAmount(dto) && (entity.getAmount() >= dto.getAmount())) {
            return true;
        } else {
            throw new IllegalArgumentException(
                    ExceptionMessage.NOT_ENOUGH_AMOUNT.setId(entity.getId()).getModifiedMessage());
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
            throw new IllegalArgumentException(ExceptionMessage.BLANK_NAME.getMessage());
        }
        NomenclatureEntity entity = nomenclatureRepository.getById(dto.getId());
        if (entity == null) {
            throw new ResourceNotFoundException("Nomenclature", "id", dto.getId());
        }
        return entity;
    }

    @Override
    public List<NomenclatureDTO> findAllByNameContainingAndCodeContaining(String name, String code) {
        List<NomenclatureEntity> entities = null;
        boolean isNameBlank = Strings.isBlank(name);
        boolean isCodeBlank = Strings.isBlank(code);

        if (isNameBlank && isCodeBlank) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_NAME_AND_CODE.getMessage());
        }
        name = name.toUpperCase(Locale.ROOT);
        if (isCodeBlank) {
            entities = nomenclatureRepository.findAllByNameContaining(name);
        }
        code = code.toUpperCase(Locale.ROOT);
        boolean isLookingForNullCode = "NULL".equals(code);
        if (isNameBlank) {
            entities = isLookingForNullCode
                    ? nomenclatureRepository.findAllByCode(null)
                    : nomenclatureRepository.findAllByCodeContaining(code);
        }
        if (!isNameBlank && !isCodeBlank) {
            entities = isLookingForNullCode
                    ? nomenclatureRepository.findAllByNameContainingAndCodeContaining(name, null)
                    : nomenclatureRepository.findAllByNameContainingAndCodeContaining(name, code);
        }

        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO delete(Long id) {
        NomenclatureEntity entity = getEntityById(id);
        if (recordRepository.existsByNomenclature_Id(id)) {
            throw new ProhibitedRemovingException(ExceptionMessage.NOMENCLATURE_HAS_RECORDS.setId(id).getModifiedMessage());
        }
        if (entity.getAmount() <= 0) {
            throw new ProhibitedRemovingException(ExceptionMessage.NOMENCLATURE_AMOUNT_IS_POSITIVE.setId(id).getModifiedMessage());
        }
        nomenclatureRepository.delete(entity);
        return mapper.convert(entity);
    }
}
