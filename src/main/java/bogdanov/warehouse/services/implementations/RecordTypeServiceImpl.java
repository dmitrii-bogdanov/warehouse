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

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordTypeServiceImpl implements RecordTypeService {

    private final RecordTypeRepository recordTypeRepository;
    private final Mapper mapper;

    @Override
    public RecordTypeDTO getById(Long id) {
        if (id == null) {
            throw new NullIdException("RecordType id is null");
        }
        Optional<RecordTypeEntity> entity = recordTypeRepository.findById(id);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
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
        Optional<RecordTypeEntity> entity = recordTypeRepository.findByName(name);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
        } else {
            throw new ResourceNotFoundException(
                    "ResourceType with name : " + name + " not found");
        }
    }

    @Override
    public List<RecordTypeDTO> getAll() {
        return recordTypeRepository.findAll().stream().map(mapper::convert).toList();
    }
}
