package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.entities.ReverseRecordEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.*;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.RecordService;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

//TODO hide deleted records from usual call of getAll()
@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final Mapper mapper;
    private final NomenclatureService nomenclatureService;
    private final UserRepository userRepository;
    private final RecordTypeService recordTypeService;
    private final ReverseRecordRepository reverseRecordRepository;

    @Override
    public RecordDTO add(RecordInputDTO record, String username) {
        RecordEntity entity = mapper.convert(record);
        entity.setUser(userRepository.findByUsername(username));
        return mapper.convert(add(entity));
    }

    private RecordEntity add(RecordEntity entity) {
        entity.setTime(LocalDateTime.now());
        NomenclatureDTO nomenclatureDTO = mapper.convert(entity.getNomenclature());
        nomenclatureDTO.setAmount(entity.getAmount());
        switch (entity.getType().getName()) {
            case "RECEPTION" -> nomenclatureService.addAmount(nomenclatureDTO);
            case "RELEASE" -> nomenclatureService.subtractAmount(nomenclatureDTO);
        }
        return recordRepository.save(entity);
    }

    @Override
    public RecordDTO revert(Long id, String username) {
        RecordEntity revertedRecord = getEntityById(id);
        RecordEntity generatedRecord = new RecordEntity();
        generatedRecord.setUser(userRepository.findByUsername(username));
        generatedRecord.setNomenclature(revertedRecord.getNomenclature());
        generatedRecord.setAmount(revertedRecord.getAmount());
        switch (revertedRecord.getType().getName()) {
            case "RECEPTION" -> generatedRecord.setType(recordTypeService.getEntityByName("RELEASE"));
            case "RELEASE" -> generatedRecord.setType(recordTypeService.getEntityByName("RECEPTION"));
        }
        generatedRecord = add(generatedRecord);
        ReverseRecordEntity reverseRecord = new ReverseRecordEntity(
                revertedRecord, generatedRecord, generatedRecord.getTime(), generatedRecord.getUser());
        reverseRecordRepository.save(reverseRecord);
        return mapper.convert(generatedRecord);
    }

    @Override
    public RecordDTO update(Long id, String username, RecordInputDTO record) {
        revert(id, username);
        return add(record, username);
    }

    @Override
    public List<RecordDTO> getAll() {
        return recordRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByUserId(Long id) {
        if (id == null) {
            return Collections.EMPTY_LIST;
        }
        return recordRepository.findAllByUser_Id(id).stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByUserUsername(String username) {
        if (Strings.isBlank(username)) {
            return Collections.EMPTY_LIST;
        }
        return recordRepository.findAllByUser_UsernameEquals(username.toUpperCase(Locale.ROOT))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByType(String type) {
        if (Strings.isBlank(type)) {
            return Collections.EMPTY_LIST;
        }
        return recordRepository.findAllByType_NameEquals(type).stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByNomenclatureId(Long nomenclatureId) {
        return recordRepository.findAllByNomenclature_Id(nomenclatureId).stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByNomenclatureName(String nomenclatureName) {
        if (Strings.isBlank(nomenclatureName)) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_NAME.getMessage());
        }
        return recordRepository.findAllByNomenclature_NameEquals(nomenclatureName).stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByNomenclatureCode(String nomenclatureCode) {
        if (Strings.isBlank(nomenclatureCode)) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_CODE.getMessage());
        }
        return recordRepository.findAllByNomenclature_CodeEquals(nomenclatureCode).stream().map(mapper::convert).toList();
    }

    @Override
    public RecordDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public RecordEntity getEntityById(Long id) {
        Optional<RecordEntity> entity = recordRepository.findById(id);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new ResourceNotFoundException("Record with id : " + id + " not found");
        }
    }

    @Override
    public List<RecordDTO> findAllByDate(LocalDate date) {
        return findAllByDateBetween(date, date.plusDays(1));
    }

    @Override
    public List<RecordDTO> findAllByDateBetween(LocalDate start, LocalDate end) {
        return recordRepository.
                findAllByTimeBetween(start.atStartOfDay(), end.atStartOfDay())
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<ReverseRecordDTO> getAllReverseRecords() {
        return reverseRecordRepository.findAll().stream().map(mapper::convert).toList();
    }
}
