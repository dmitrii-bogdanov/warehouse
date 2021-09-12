package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.dto.RecordTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecordTypeService {

    List<RecordTypeDTO> getAll();

    RecordTypeDTO getById(Long id);

    RecordTypeEntity getEntityById(Long id);

    RecordTypeDTO getByName(String name);

    RecordTypeEntity getEntityByName(String name);

}
