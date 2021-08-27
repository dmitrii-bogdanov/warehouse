package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecordService {

    RecordDTO add(RecordInputDTO record, UserDetails user);

    void delete(RecordOutputDTO record);

    List<RecordDTO> getAll();

    List<RecordDTO> findAllByUserId(Long id);

    List<RecordDTO> findAllByUserUsername(String username);

    List<RecordDTO> findAllByType(String type);

    List<RecordDTO> findAllByNomenclatureId(Long nomenclatureId);

    List<RecordDTO> findAllByNomenclatureName(String nomenclatureName);

    List<RecordDTO> findAllByNomenclatureCode(String nomenclatureCode);

}
