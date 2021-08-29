package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface RecordService {

    RecordDTO add(RecordInputDTO record, String username);

    List<RecordDTO> getAll();

    List<RecordDTO> findAllByUserId(Long id);

    List<RecordDTO> findAllByUserUsername(String username);

    List<RecordDTO> findAllByType(String type);

    List<RecordDTO> findAllByNomenclatureId(Long nomenclatureId);

    List<RecordDTO> findAllByNomenclatureName(String nomenclatureName);

    List<RecordDTO> findAllByNomenclatureCode(String nomenclatureCode);

    RecordDTO getById(Long id);

    RecordEntity getEntityById(Long id);

    List<RecordDTO> findAllByDate(LocalDate date);

    List<RecordDTO> findAllByDateBetween(LocalDate start, LocalDate end);

    RecordDTO delete(Long id);
}
