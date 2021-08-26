package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecordService {

    RecordDTO add(RecordInputDTO record, UserDetails user);

    List<RecordDTO> getAll();

    List<RecordDTO> findAllByUserId(Long id);

    List<RecordDTO> findAllByType(String type);

    List<RecordDTO> findAllByNomenclatureId(Long nomenclatureId);

    List<RecordDTO> findAllByNomenclatureName(String nomenclatureName);

    List<RecordDTO> findAllByNomenclatureCode(String nomenclatureCode);

}
