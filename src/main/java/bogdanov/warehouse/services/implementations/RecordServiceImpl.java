package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import bogdanov.warehouse.exceptions.IncorectRecordFieldsException;
import bogdanov.warehouse.exceptions.NullIdException;
import bogdanov.warehouse.exceptions.ProhibitedRemovingException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
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
    private final NomenclatureServiceImpl nomenclatureService;
    //TODO delete! For Test Only
    private final UserRepository userRepository;
    private final RecordTypeService recordTypeService;

    @Override
    public RecordDTO add(RecordInputDTO record, String username) {
        RecordEntity entity = mapper.convert(record);
        entity.setTime(LocalDateTime.now());
        entity.setUser(userRepository.findByUsername(username));
        NomenclatureDTO nomenclatureDTO = mapper.convert(entity.getNomenclature());
        nomenclatureDTO.setAmount(entity.getAmount());
        switch (entity.getType().getName()) {
            case "RECEPTION" -> nomenclatureService.addAmount(nomenclatureDTO);
            case "RELEASE" -> nomenclatureService.subtractAmount(nomenclatureDTO);
        }
        return mapper.convert(recordRepository.save(entity));
    }

    @Override
    public RecordDTO delete(Long id) {
        if (id == null) {
            throw new NullIdException("Id is absent");
        }
        Optional<RecordEntity> optionalEntity = recordRepository.findById(id);
        if (optionalEntity.isPresent()) {
            RecordEntity entity = optionalEntity.get();

            if (LocalDateTime.now().minusHours(24).compareTo(entity.getTime()) > 0) {
                entity.setType(mapper.convert(recordTypeService.getByName("DELETED")));
                entity = recordRepository.save(entity);
            } else {
                recordRepository.delete(entity);
            }
            return mapper.convert(entity);
        } else {
            throw new ResourceNotFoundException("Record with id : " + id + " not found");
        }
    }

    @Override
    public List<RecordDTO> getAll() {
        return recordRepository.findAll().stream().filter(this::filterDeletedRecords).map(mapper::convert).toList();
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
        if (nomenclatureId == null) {
            return Collections.EMPTY_LIST;
        }
        return recordRepository.findAllByNomenclature_Id(nomenclatureId).stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByNomenclatureName(String nomenclatureName) {
        if (Strings.isBlank(nomenclatureName)) {
            return Collections.EMPTY_LIST;
        }
        return recordRepository.findAllByNomenclature_NameEquals(nomenclatureName).stream().map(mapper::convert).toList();
    }

    @Override
    public List<RecordDTO> findAllByNomenclatureCode(String nomenclatureCode) {
        if (Strings.isBlank(nomenclatureCode)) {
            return Collections.EMPTY_LIST;
        }
        return recordRepository.findAllByNomenclature_CodeEquals(nomenclatureCode).stream().map(mapper::convert).toList();
    }

    private boolean filterDeletedRecords(RecordEntity entity) {
        return !entity.getType().getName().equals("DELETED");
    }

    @Override
    public RecordDTO getById(Long id) {
        if (id == null) {
            throw new NullIdException("Id value is missing");
        }
        Optional<RecordEntity> entity = recordRepository.findById(id);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
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
}
