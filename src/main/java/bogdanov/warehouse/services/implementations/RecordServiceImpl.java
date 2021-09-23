package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.*;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.ReverseRecordDTO;
import bogdanov.warehouse.dto.search.SearchRecordDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.WarehouseExeption;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.RecordService;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final Mapper mapper;
    private final NomenclatureService nomenclatureService;
    private final RecordTypeService recordTypeService;
    private final ReverseRecordRepository reverseRecordRepository;
    private final InternalUserService userService;

    private static final String RECEPTION = "RECEPTION";
    private static final String RELEASE = "RELEASE";
    private static final String RECORD = "Record";
    private static final String ID = "id";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String UNIQUE_KEY_VIOLATION_SUBSTRING = "ON PUBLIC.REVERSE_RECORDS(";
    private static final String REVERTED_RECORD_ID = "REVERTED_RECORD_ID";

    private void checkDtoNotNull(Object dto) {
        if (dto == null) {
            throw new ArgumentException(ExceptionType.NO_OBJECT_WAS_PASSED);
        }
    }

    private void checkId(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
    }

    @Override
    public RecordDTO add(RecordDTO record, String username) {
        checkDtoNotNull(record);
        RecordEntity entity = mapper.convert(record);
        entity.setNomenclature(nomenclatureService.getEntityById(record.getNomenclatureId()));
        entity.setType(recordTypeService.getEntityByName(record.getType()));
        entity.setUser(userService.getEntityByUsername(username));
        return mapper.convert(add(entity));
    }

    private RecordEntity add(RecordEntity entity) {
        entity.setTime(LocalDateTime.now());
        NomenclatureDTO nomenclatureDTO = mapper.convert(entity.getNomenclature());
        nomenclatureDTO.setAmount(entity.getAmount());
        switch (entity.getType().getName()) {
            case RECEPTION -> nomenclatureService.addAmount(nomenclatureDTO);
            case RELEASE -> nomenclatureService.subtractAmount(nomenclatureDTO);
        }
        return recordRepository.save(entity);
    }

    private void checkAlreadyReverted(Long id) {
        if (reverseRecordRepository.findByRevertedRecord_Id(id).isPresent()) {
            throw new ArgumentException(ExceptionType.ALREADY_REVERTED_RECORD.setId(id));
        }
    }

    @Override
    public RecordDTO revert(Long id, String username) {
        RecordEntity revertedRecord = getEntityById(id);
        checkAlreadyReverted(id);
        RecordEntity generatedRecord = new RecordEntity();
        generatedRecord.setUser(userService.getEntityByUsername(username));
        generatedRecord.setNomenclature(revertedRecord.getNomenclature());
        generatedRecord.setAmount(revertedRecord.getAmount());
        switch (revertedRecord.getType().getName()) {
            case RECEPTION -> generatedRecord.setType(recordTypeService.getEntityByName(RELEASE));
            case RELEASE -> generatedRecord.setType(recordTypeService.getEntityByName(RECEPTION));
        }
        generatedRecord = add(generatedRecord);
        ReverseRecordEntity reverseRecord = new ReverseRecordEntity(
                null, revertedRecord, generatedRecord, generatedRecord.getTime(), generatedRecord.getUser());
        reverseRecordRepository.save(reverseRecord);
        return mapper.convert(generatedRecord);
    }

    //TODO need to add some verifier to not revert if it cannot be added
    @Override
    public RecordDTO update(Long id, String username, RecordDTO record) {
        checkDtoNotNull(record);
        RecordDTO generated = revert(id, username);
        try {
            return add(record, username);
        } catch (ArgumentException e) {
            revert(generated.getId(), username);
            throw e;
        }
    }

    @Override
    public List<RecordDTO> getAll() {
        return new LinkedList<>(recordRepository.findAll().stream().map(mapper::convert).toList());
    }

    @Override
    public RecordDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public RecordEntity getEntityById(Long id) {
        checkId(id);
        return recordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECORD, ID, id));
    }

    @Override
    public List<ReverseRecordDTO> getAllReverseRecords() {
        return reverseRecordRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> search(SearchRecordDTO dto) {

        checkDtoNotNull(dto);

        boolean isTypeBlank = Strings.isBlank(dto.getType());
        boolean isFromDateAbsent = dto.getFromDate() == null;
        boolean isToDateAbsent = dto.getToDate() == null;
        boolean isNomenclatureListAbsent = dto.getNomenclatureId() == null || dto.getNomenclatureId().isEmpty();
        boolean isUserListAbsent = dto.getUserId() == null || dto.getUserId().isEmpty();

        if (isTypeBlank && isFromDateAbsent && isToDateAbsent && isNomenclatureListAbsent && isUserListAbsent) {
            throw new ArgumentException(ExceptionType.NO_PARAMETER_IS_PRESENT);
        }

        List<RecordTypeEntity> types = new LinkedList<>();
        List<NomenclatureEntity> nomenclature = new LinkedList<>();
        List<UserEntity> users = new LinkedList<>();
        LocalDateTime from;
        LocalDateTime to;
        if (isTypeBlank) {
            types.add(recordTypeService.getEntityByName(RECEPTION));
            types.add(recordTypeService.getEntityByName(RELEASE));
        } else {
            types.add(recordTypeService.getEntityByName(dto.getType()));
        }
        if (isFromDateAbsent) {
            from = LocalDateTime.of(0, 1, 1, 0, 0, 0, 0);
        } else {
            from = LocalDateTime.of(dto.getFromDate(), LocalTime.of(0, 0, 0, 0));
        }
        if (isToDateAbsent) {
            to = LocalDateTime.of(2200, 12, 31, 23, 59, 59, 999_999_000);
        } else {
            to = LocalDateTime.of(dto.getToDate(), LocalTime.of(23, 59, 59, 999_999_000));
        }
        if (from.isAfter(to)) {
            throw new ArgumentException(ExceptionType.INCORRECT_RANGE.setFrom(FROM_DATE).setTo(TO_DATE));
        }
        if (!isNomenclatureListAbsent) {
            for (Long id : dto.getNomenclatureId()) {
                nomenclature.add(nomenclatureService.getEntityById(id));
            }
        }
        if (!isUserListAbsent) {
            for (Long id : dto.getUserId()) {
                users.add(userService.getEntityById(id));
            }
        }

        List<RecordEntity> entities;

        if (isTypeBlank && isNomenclatureListAbsent && isUserListAbsent) {
            entities = recordRepository.findAllByTimeBetween(from, to);
        } else if (isNomenclatureListAbsent && isUserListAbsent) {
            entities = recordRepository.findAllByTypeInAndTimeBetween(types, from, to);
        } else if (isUserListAbsent) {
            entities = recordRepository.findAllByTypeInAndNomenclatureInAndTimeBetween(types, nomenclature, from, to);
        } else if (isNomenclatureListAbsent) {
            entities = recordRepository.findAllByTypeInAndUserInAndTimeBetween(types, users, from, to);
        } else {
            entities = recordRepository.findAllByTypeInAndNomenclatureInAndUserInAndTimeBetween(
                    types, nomenclature, users, from, to);
        }

        return entities.stream().map(mapper::convert).toList();

    }

    @Override
    public boolean existsByUserId(long id) {
        return recordRepository.existsByUser_Id(id);
    }
}
