package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import bogdanov.warehouse.exceptions.IncorectRecordFieldsException;
import bogdanov.warehouse.exceptions.NullIdException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.RecordService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final Mapper mapper;
    private final NomenclatureServiceImpl nomenclatureService;
    //TODO delete! For Test Only
    private final UserRepository userRepository;

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
    public void delete(RecordOutputDTO record) {
        if (record.getId() == null) {
            throw new NullIdException("Id is absent");
        }
        Optional<RecordEntity> optionalEntity = recordRepository.findById(record.getId());
        if (optionalEntity.isPresent()) {
            RecordEntity fromDTO = mapper.convert(record);
            RecordEntity entity = optionalEntity.get();
            boolean isNomenclatureCorrect = entity.getNomenclature().getId().equals(fromDTO.getNomenclature().getId());
            boolean isTimeCorrect = entity.getTime().equals(fromDTO.getTime());
            boolean isAmountCorrect = entity.getAmount().equals(fromDTO.getAmount());
            boolean isTypeCorrect = entity.getType().equals(fromDTO.getType());
            if (isNomenclatureCorrect && isAmountCorrect && isTypeCorrect && isTimeCorrect) {
                recordRepository.delete(entity);
            } else {
                throw new IncorectRecordFieldsException(
                        "Records fields (id, nomenclature, type, amount, time) are incorrect");
            }
        } else {
            throw new ResourceNotFoundException("Record with id : " + record.getId() + " not found");
        }
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
}
