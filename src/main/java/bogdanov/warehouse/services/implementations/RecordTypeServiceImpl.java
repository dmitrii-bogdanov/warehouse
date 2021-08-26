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
    private final Map<String, RecordTypeDTO> types = new HashMap<>();

    public RecordTypeServiceImpl(RecordTypeRepository recordTypeRepository) {
        this.recordTypeRepository = recordTypeRepository;
        recordTypeRepository.findAll().forEach(e -> types.put(e.getName(), new RecordTypeDTO(e.getId(), e.getName())));
    }

    @Override
    public RecordTypeDTO getById(Long id) {
        if (id == null) {
            throw new NullIdException("RecordType id is null");
        }
        Optional<RecordTypeEntity> entity = recordTypeRepository.findById(id);
        if (entity.isPresent()) {
            return new RecordTypeDTO(entity.get().getId(), entity.get().getName());
        } else {
            throw new ResourceNotFoundException(
                    "ResourceType with id : " + id + " not found");
        }
    }

    @Override
    public RecordTypeDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new BlankNameException("RecordType name is blank");
        }
        name = name.toUpperCase(Locale.ROOT);
        if (types.containsKey(name)) {
            return types.get(name);
        } else {
            throw new ResourceNotFoundException(
                    "ResourceType with name : " + name + " not found");
        }
    }

    @Override
    public List<RecordTypeDTO> getAll() {
        return types.values().stream().toList();
    }
}
