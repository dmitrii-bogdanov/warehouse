package bogdanov.warehouse.services.implementations.cashed;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.database.repositories.RecordTypeRepository;
import bogdanov.warehouse.dto.RecordTypeDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.RecordTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Primary
@Qualifier("cached")
@RequiredArgsConstructor
public class RecordTypeServiceImpl implements RecordTypeService {

    private final RecordTypeRepository recordTypeRepository;
    private final ObjectMapper objectMapper;

    private static final String RECORD_TYPE = "RecordType";
    private static final String ID = "id";
    private static final String NAME = "name";

    private RecordTypeDTO convert(RecordTypeEntity entity) {
        return objectMapper.convertValue(entity, RecordTypeDTO.class);
    }

    @Override
    public List<RecordTypeDTO> getAll() {
        return recordTypeRepository.findAll().stream().map(this::convert).toList();
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
        return recordTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RECORD_TYPE, ID, id));
    }

    @Override
    public RecordTypeDTO getByName(String name) {
        return convert(getEntityByName(name));
    }

    @Override
    @Cacheable("record_types")
    public RecordTypeEntity getEntityByName(String name) {
        if (Strings.isBlank(name)) {
            throw new ArgumentException(ExceptionType.BLANK_ENTITY_NAME.setEntity(RecordTypeEntity.class));
        }

        return recordTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(RECORD_TYPE, NAME, name.toUpperCase(Locale.ROOT)));
    }


}
