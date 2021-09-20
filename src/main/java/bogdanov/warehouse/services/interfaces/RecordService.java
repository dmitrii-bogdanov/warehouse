package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.ReverseRecordDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface RecordService {

    RecordDTO add(RecordDTO record, String username);

    List<RecordDTO> getAll();

    List<RecordDTO> search(Long nomenclatureId, Long userId, String type, LocalDate dateFrom, LocalDate dateTo);

    RecordDTO getById(Long id);

    RecordEntity getEntityById(Long id);

    RecordDTO revert(Long id, String username);

    RecordDTO update(Long id, String username, RecordDTO record);

    List<ReverseRecordDTO> getAllReverseRecords();

    boolean existsByUserId(Long id);
}
