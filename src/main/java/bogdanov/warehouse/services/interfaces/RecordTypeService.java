package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.dto.RecordTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecordTypeService {

    RecordTypeDTO getById(Long id);

    RecordTypeDTO getByName(String name);

    List<RecordTypeDTO> getAll();

}
