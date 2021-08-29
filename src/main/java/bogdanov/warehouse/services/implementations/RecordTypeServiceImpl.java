package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.database.repositories.RecordTypeRepository;
import bogdanov.warehouse.dto.RecordTypeDTO;
import bogdanov.warehouse.exceptions.BlankNameException;
import bogdanov.warehouse.exceptions.NullIdException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@RequiredArgsConstructor
public class RecordTypeServiceImpl implements RecordTypeService {

    private final RecordTypeRepository recordTypeRepository;
    private final Map<String, RecordTypeEntity> entities = new HashMap<>();

    public RecordTypeServiceImpl(RecordTypeRepository recordTypeRepository) {
        this.recordTypeRepository = recordTypeRepository;
        recordTypeRepository.findAll().forEach(e -> entities.put(e.getName(), e));
    }

    @Override
    public RecordTypeDTO getById(Long id) {
        RecordTypeEntity entity = getEntityById(id);
        return new RecordTypeDTO(entity.getId(), entity.getName());
    }

    @Override
    public RecordTypeEntity getEntityById(Long id) {
        if (id == null) {
            throw new NullIdException();
        }
        List<RecordTypeEntity> tmp = entities.values().stream().filter(e -> e.getId() == id).toList();
        if (!tmp.isEmpty()) {
            return tmp.get(0);
        } else {
            throw new ResourceNotFoundException("RecordType","id",id);
        }
    }

    @Override
    public RecordTypeEntity getEntityByName(String name) {
        if (Strings.isBlank(name)) {
            throw new BlankNameException(RecordTypeEntity.class);
        }
        name = name.toUpperCase(Locale.ROOT);
        if (entities.containsKey(name)) {
            return entities.get(name);
        } else {
            throw new ResourceNotFoundException("RecordType", "name", name);
        }
    }

    @Override
    public RecordTypeDTO getByName(String name) {
        RecordTypeEntity entity = getEntityByName(name);
        return new RecordTypeDTO(entity.getId(), entity.getName());
    }

    @Override
    public List<RecordTypeDTO> getAll() {
        return entities.values().stream().map(e -> new RecordTypeDTO(e.getId(), e.getName())).toList();
    }
}
