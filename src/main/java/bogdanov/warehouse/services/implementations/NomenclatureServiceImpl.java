package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.repositories.NomenclatureRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.PropertyValueException;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class NomenclatureServiceImpl implements NomenclatureService {

    private final NomenclatureRepository nomenclatureRepository;
    private final Mapper mapper;

    private static final String DATA_INTEGRITY_EXCEPTION_SUBSTRING = "ON PUBLIC.NOMENCLATURE(";
    private static final String EXISTING_REFERENCE_SUBSTRING = "REFERENCES PUBLIC.NOMENCLATURE";
    private static final String RECORD_FOREIGN_KEY_SUBSTRING = "PUBLIC.RECORDS FOREIGN KEY(NOMENCLATURE";
    private static final String NOMENCLATURE = "Nomenclature";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CODE = "code";
    private static final String RESERVED_NULL_CODE_VALUE = "NULL";
    private static final Long ZERO = 0L;
    private static final String MIN_AMOUNT = "minAmount";
    private static final String MAX_AMOUNT = "maxAmount";
    private static final String UNKNOWN_FIELD = "unknown field";
    private static final String REGEX_TO_DELETE_NESTED_EXCEPTIONS = ";.*";
    private static final String SEMICOLON = ";";
    private static final String APOSTROPHE = "\"";
    private static final String SPACE_STR = " ";
    private static final String COMMENT_FOR_GET_BY_CODE_NULL = "If system allows to use blank codes, try ?search?code=null";

    //region Utility Methods
    private boolean isCodeNotReserved(NomenclatureDTO dto) {
        return isCodeNotReserved(dto.getCode());
    }

    private boolean isCodeNotReserved(String code) {
        if (RESERVED_NULL_CODE_VALUE.equalsIgnoreCase(code)) {
            throw new ArgumentException(ExceptionType.RESERVED_VALUE
                    .setFieldName(CODE).setFieldValue(RESERVED_NULL_CODE_VALUE));
        }
        return true;
    }

    private void checkAmountPositive(Long amount) {
        if (amount == null || amount <= 0) {
            throw new ArgumentException(ExceptionType.NOT_POSITIVE_AMOUNT);
        }
    }


    private void checkAmountAvailable(Long amount, NomenclatureEntity entity) {
        checkAmountPositive(amount);
        if ((entity.getAmount() < amount)) {
            throw new ArgumentException(ExceptionType.NOT_ENOUGH_AMOUNT.setId(entity.getId()).setFieldValue(amount));
        }
    }

    private NomenclatureEntity add(Long amount, NomenclatureEntity entity) {
        checkAmountPositive(amount);
        long sum = entity.getAmount() + amount;
        if (sum < 0) {
            throw new ArgumentException(ExceptionType.LONG_VALUE_OVERFLOW);
        }
        entity.setAmount(sum);
        return entity;
    }

    private NomenclatureEntity take(Long amount, NomenclatureEntity entity) {
        checkAmountAvailable(amount, entity);
        entity.setAmount(entity.getAmount() - amount);
        return entity;
    }

    private RuntimeException wrapException(DataIntegrityViolationException e) {
        return wrapException(e, false, null);
    }

    private RuntimeException wrapException(DataIntegrityViolationException e, boolean isUpdate, Collection<NomenclatureDTO> dto) {
        String message = e.getMessage();
        message = message == null ? Strings.EMPTY : message;
        if (message.contains(EXISTING_REFERENCE_SUBSTRING)) {
            if (message.contains(RECORD_FOREIGN_KEY_SUBSTRING)) {
                return new ProhibitedRemovingException(ExceptionType.NOMENCLATURE_HAS_RECORDS);
            }
        }
        if (PropertyValueException.class.equals(e.getCause().getClass())) {
            message = message
                    .replaceAll(REGEX_TO_DELETE_NESTED_EXCEPTIONS, Strings.EMPTY)
                    .replace(NomenclatureEntity.class.getName(), NOMENCLATURE);
            return new ArgumentException(ExceptionType.NULL_PROPERTY_WAS_PASSED.setMessage(message));
        }

        StringBuilder sb = new StringBuilder(message);
        int index;
        if ((index = sb.indexOf(DATA_INTEGRITY_EXCEPTION_SUBSTRING)) > -1) {
            ExceptionType type = null;
            sb.delete(0, index + DATA_INTEGRITY_EXCEPTION_SUBSTRING.length());
            sb.delete(sb.indexOf(SEMICOLON), sb.length());
            long id = Long.parseLong(sb.substring(1 + sb.lastIndexOf(SPACE_STR), sb.indexOf(APOSTROPHE)));//was 13
            Optional<NomenclatureEntity> entity = nomenclatureRepository.findById(id);
            String field = UNKNOWN_FIELD;
            if (entity.isPresent()) {
                if (CODE.equalsIgnoreCase(sb.substring(0, CODE.length()))) {
                    type = ExceptionType.ALREADY_RECORDED_NAME_OR_CODE
                            .setFieldName(field = CODE).setFieldValue(entity.get().getCode()).setId(id);
                } else if (NAME.equalsIgnoreCase(sb.substring(0, NAME.length()))) {
                    type = ExceptionType.ALREADY_RECORDED_NAME_OR_CODE
                            .setFieldName(field = NAME).setFieldValue(entity.get().getName()).setId(id);
                }

                if (isUpdate) {
                    List<String> strings = null;
                    if (CODE.equals(field)) {
                        strings = dto.stream().map(NomenclatureDTO::getCode).filter(Strings::isNotEmpty).toList();
                    } else if (NAME.equals(field)) {
                        strings = dto.stream().map(NomenclatureDTO::getName).toList();
                    }
                    if (strings != null && strings.size() > strings.stream().distinct().toList().size()) {
                        type = ExceptionType.LIST_CONTAINS_REPEATING_VALUES
                                .setFieldName(field.toLowerCase(Locale.ROOT));
                    }
                }

            } else {
                type = ExceptionType.LIST_CONTAINS_REPEATING_VALUES
                        .setFieldName(field.toLowerCase(Locale.ROOT));
            }

            return new ArgumentException(type);
        } else {
            return e;
        }
    }

    private void checkListNotEmpty(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            throw new ArgumentException(ExceptionType.NO_OBJECT_WAS_PASSED);
        }
    }

    private void checkName(String name) {
        if (Strings.isBlank(name)) {
            throw new ArgumentException(ExceptionType.BLANK_NAME);
        }
    }

    private void checkCodeForGet(String code) {
        if (Strings.isBlank(code)) {
            throw new ArgumentException(ExceptionType.BLANK_CODE.addComment(COMMENT_FOR_GET_BY_CODE_NULL));
        }
    }
    //endregion

    @Override
    public List<NomenclatureDTO> createNew(Collection<NomenclatureDTO> nomenclature) {
        checkListNotEmpty(nomenclature);
        try {
            return nomenclatureRepository
                    .saveAll(nomenclature.stream().filter(this::isCodeNotReserved).map(mapper::convert).toList())
                    .stream().map(mapper::convert).toList();
        } catch (DataIntegrityViolationException e) {
            throw wrapException(e);
        }
    }

    @Override
    public NomenclatureDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public NomenclatureEntity getEntityById(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
        return nomenclatureRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException(NOMENCLATURE, ID, id));
    }

    @Override
    public List<NomenclatureDTO> update(List<NomenclatureDTO> nomenclature) {
        checkListNotEmpty(nomenclature);
        try {
            return nomenclatureRepository.saveAll(
                    nomenclature
                            .stream()
                            .filter(this::isCodeNotReserved)
                            .map(n -> {
                                NomenclatureEntity entity = getEntityById(n.getId());
                                NomenclatureEntity input = mapper.convert(n);
                                entity.setName(input.getName());
                                entity.setCode(input.getCode());
                                return entity;
                            })
                            .toList()
            )
                    .stream()
                    .map(mapper::convert)
                    .toList();
        } catch (DataIntegrityViolationException e) {
            throw wrapException(e, true, nomenclature);
        }
    }

    @Override
    public NomenclatureDTO getByName(String name) {
        checkName(name);
        return mapper.convert(
                nomenclatureRepository.findByNameIgnoreCase(name)
                        .orElseThrow(() -> new ResourceNotFoundException(NOMENCLATURE, NAME, name)));
    }

    @Override
    public NomenclatureDTO getByCode(String code) {
        checkCodeForGet(code);
        isCodeNotReserved(code);
        return mapper.convert(
                nomenclatureRepository.findByCodeIgnoreCase(code)
                        .orElseThrow(() -> new ResourceNotFoundException(NOMENCLATURE, CODE, code)));
    }

    @Override
    public List<NomenclatureDTO> getAll() {
        return nomenclatureRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<NomenclatureDTO> getAllAvailable() {
        return nomenclatureRepository.findAllByAmountGreaterThan(ZERO).stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO addAmount(NomenclatureDTO nomenclature) {
        NomenclatureEntity entity = getEntityById(nomenclature.getId());
        return mapper.convert(
                nomenclatureRepository.save(
                        add(nomenclature.getAmount(), entity)));
    }

    @Override
    public NomenclatureDTO subtractAmount(NomenclatureDTO nomenclature) {
        NomenclatureEntity entity = getEntityById(nomenclature.getId());
        return mapper.convert(
                nomenclatureRepository.save(
                        take(nomenclature.getAmount(), entity)));

    }

    @Override
    public List<NomenclatureDTO> search(String name, String code, Long minAmount, Long maxAmount) {

        boolean isNameBlank = Strings.isBlank(name);
        boolean isCodeBlank = Strings.isBlank(code);
        boolean isMinAmountAbsent = minAmount == null;
        boolean isMaxAmountAbsent = maxAmount == null;
        boolean shouldCodeBeNull = RESERVED_NULL_CODE_VALUE.equalsIgnoreCase(code);

        if (isNameBlank && isCodeBlank && isMinAmountAbsent && isMaxAmountAbsent) {
            throw new ArgumentException(ExceptionType.NO_PARAMETER_IS_PRESENT);
        }
        if (isNameBlank) {
            name = Strings.EMPTY;
        }
        if (isCodeBlank) {
            code = Strings.EMPTY;
        }
        if (isMinAmountAbsent) {
            minAmount = ZERO;
        } else if (minAmount < 0) {
            checkAmountPositive(minAmount);
        }
        if (isMaxAmountAbsent) {
            maxAmount = Long.MAX_VALUE;
        } else if (maxAmount < 0) {
            checkAmountPositive(maxAmount);
        }
        if (minAmount > maxAmount) {
            throw new ArgumentException(ExceptionType.INCORRECT_RANGE.setFrom(MIN_AMOUNT).setTo(MAX_AMOUNT));
        }

        List<NomenclatureEntity> entities;
        if (shouldCodeBeNull) {
            entities = nomenclatureRepository.findAllByNameContainingIgnoreCaseAndCodeIsNullAndAmountBetween(
                    name, minAmount, maxAmount);
        } else if (isCodeBlank) {
            entities = nomenclatureRepository.findAllByNameContainingIgnoreCaseAndAmountBetween(
                    name, minAmount, maxAmount);
        } else {
            entities = nomenclatureRepository.findAllByNameContainingIgnoreCaseAndCodeContainingIgnoreCaseAndAmountBetween(
                    name, code, minAmount, maxAmount);
        }

        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public NomenclatureDTO delete(Long id) {
        NomenclatureEntity entity = getEntityById(id);
        if (entity.getAmount() > 0) {
            throw new ProhibitedRemovingException(ExceptionType.NOMENCLATURE_AMOUNT_IS_POSITIVE.setId(id));
        }
        try {
            nomenclatureRepository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw wrapException(e);
        }
        return mapper.convert(entity);
    }
}
