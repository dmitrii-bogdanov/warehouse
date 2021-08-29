package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.dto.RecordTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecordTypeService {

    RecordTypeDTO getById(Long id);

    RecordTypeEntity getEntityById(Long id);

    RecordTypeEntity getEntityByName(String name);

    RecordTypeDTO getByName(String name);

    List<RecordTypeDTO> getAll();

}
