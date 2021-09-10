package bogdanov.warehouse.services.implementations.cashed;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.ProhibitedRemovingException;
import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.PositionService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class    PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PersonRepository personRepository;
    private final Map<String, PositionDTO> positions = new HashMap<>();

    @Override
    @Cacheable(value = "positions", key = "#name")
    public PositionEntity add(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(
                    ExceptionMessage.BLANK_ENTITY_NAME.setEntity(PositionEntity.class).getModifiedMessage());
        }
        positions.computeIfAbsent(name, n -> {
            PositionEntity entity = positionRepository.save(new PositionEntity(name));
            return new PositionDTO(entity.getId(), entity.getName());
        });
        return positions.get(name);
    }

    @Override
    public PositionDTO add(PositionDTO position) {
        PositionEntity entity = add(position.getName());
        return new PositionDTO(entity.getId(), entity.getName());
    }

    @Override
    public List<PositionDTO> add(List<PositionDTO> positions) {
        return positions.stream().map(this::add).toList();
    }

    @Override
    public List<PositionDTO> getAll() {
        return positionRepository.findAll().stream().map(e -> new PositionDTO(e.getId(), e.getName())).toList();
    }

    @Override
    public PositionDTO getById(Long id) {
        PositionEntity entity = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION, ID, id));
        return new PositionDTO(entity.getId(), entity.getName());
    }

    @Override
    public PositionDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(
                    ExceptionMessage.BLANK_ENTITY_NAME.setEntity(PositionEntity.class).getModifiedMessage());
        }
        PositionEntity entity = positionRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION, NAME, name.toUpperCase(Locale.ROOT)));
        return new PositionDTO(entity.getId(), entity.getName());
    }

    @Override
    public List<PositionDTO> findAllByNameContaining(String partialName) {
        return positionRepository.findAllByNameContainingIgnoreCase(partialName)
                .stream().map(e -> new PositionDTO(e.getId(), e.getName())).toList();
    }

    @Override
    public PositionDTO delete(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(ExceptionMessage.BLANK_NAME.getMessage());
        }
        return delete(positionRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION, NAME, name.toUpperCase(Locale.ROOT))));
    }

    @Override
    public PositionDTO delete(Long id) {
        return delete(positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION, ID, id)));
    }

    private PositionDTO delete(PositionEntity entity) {
        if (personRepository.existsByPosition_Id(entity.getId())) {
            throw new ProhibitedRemovingException(
                    ExceptionMessage.POSITION_IS_IN_USE.setFieldValue(entity.getName()).getModifiedMessage());
        }
        positionRepository.delete(entity);
        return new PositionDTO(entity.getId(), entity.getName());
    }
}
