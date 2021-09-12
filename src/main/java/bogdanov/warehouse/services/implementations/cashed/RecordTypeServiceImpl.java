package bogdanov.warehouse.services.implementations.cashed;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.database.repositories.RecordTypeRepository;
import bogdanov.warehouse.dto.RecordTypeDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecordTypeServiceImpl implements RecordTypeService {

    private final RecordTypeRepository recordTypeRepository;
    private final ObjectMapper objectMapper;
    private final Map<String, RecordTypeEntity> entities = new HashMap<>();

    private static final String RECORD_TYPE = "RecordType";
    private static final String ID = "id";
    private static final String NAME = "name";

    public RecordTypeServiceImpl(RecordTypeRepository recordTypeRepository,
                                 ObjectMapper objectMapper) {
        this.recordTypeRepository = recordTypeRepository;
        this.objectMapper = objectMapper;
        updateCache();
    }

    private void updateCache() {
        entities.clear();
        recordTypeRepository.findAll().forEach(e -> entities.put(e.getName(), e));
    }

    private RecordTypeDTO convert(RecordTypeEntity entity) {
        return objectMapper.convertValue(entity, RecordTypeDTO.class);
    }

    @Override
    public List<RecordTypeDTO> getAll() {
        return entities.values().stream().map(this::convert).toList();
    }

    @Override
    public RecordTypeDTO getById(Long id) {
        return convert(getEntityById(id));
    }

    @Override
    public RecordTypeEntity getEntityById(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
        return entities.values().stream().filter(e -> e.getId() == id).findAny()
                .orElseThrow(() -> new ResourceNotFoundException(RECORD_TYPE, ID, id));
    }

    @Override
    public RecordTypeDTO getByName(String name) {
        return convert(getEntityByName(name));
    }

    @Override
    public RecordTypeEntity getEntityByName(String name) {
        if (Strings.isBlank(name)) {
            throw new ArgumentException(ExceptionType.BLANK_ENTITY_NAME.setEntity(RecordTypeEntity.class));
        }

        return Optional.ofNullable(entities.get(name.toUpperCase(Locale.ROOT)))
                .orElseThrow(() -> new ResourceNotFoundException(RECORD_TYPE, NAME, name.toUpperCase(Locale.ROOT)));
//        if (entities.containsKey(name)) {
//            return entities.get(name);
//        } else {
//            throw new ResourceNotFoundException(RECORD_TYPE, NAME, name);
//        }
    }
}
