package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.entities.ReverseRecordEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.ReverseRecordRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.ReverseRecordDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import bogdanov.warehouse.services.interfaces.RecordService;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private void checkDtoNotNull(RecordDTO dto) {
        if (dto == null) {
            throw new ArgumentException(ExceptionType.NO_OBJECT_WAS_PASSED);
        }
    }

    @Override
    public RecordDTO add(RecordInputDTO record, String username) {
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

    @Override
    public RecordDTO revert(Long id, String username) {
        RecordEntity revertedRecord = getEntityById(id);
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

    @Override
    public RecordDTO update(Long id, String username, RecordInputDTO record) {
        revert(id, username);
        return add(record, username);
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
        Optional<RecordEntity> entity = recordRepository.findById(id);
        if (entity.isPresent()) {
            return entity.get();
        } else {
            throw new ResourceNotFoundException(RECORD, ID, id);
        }
    }

    @Override
    public List<ReverseRecordDTO> getAllReverseRecords() {
        return reverseRecordRepository.findAll().stream().map(mapper::convert).toList();
    }

    //TODO
    @Override
    public List<RecordDTO> search(Long nomenclatureId, Long userId, String type, LocalDate dateFrom, LocalDate dateTo) {
        return null;
    }

    @Override
    public boolean existsByUserId(Long id) {
        return recordRepository.existsByUser_Id(id);
    }
}
