package bogdanov.warehouse.services.implementations.cashed;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ProhibitedRemovingException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PositionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Primary
@Qualifier("cached")
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PersonRepository personRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    private PositionServiceImpl self;

    private static final String POSITION = "Position";
    private static final String ID = "id";
    private static final String NAME = "name";

    private boolean isNameNotBlank(String name) {
        if (Strings.isBlank(name)) {
            throw new ArgumentException(ExceptionType.BLANK_ENTITY_NAME.setEntity(this.getClass()));
        }
        return true;
    }

    private PositionDTO convert(PositionEntity entity) {
        return objectMapper.convertValue(entity, PositionDTO.class);
    }

    @Override
    @Cacheable("positions")
    public PositionEntity add(String name) {
        isNameNotBlank(name);
        return positionRepository.save(new PositionEntity(name.toUpperCase(Locale.ROOT)));
    }

    @Override
    public PositionDTO add(PositionDTO position) {
        return convert(self.add(StringUtils.toRootUpperCase(position.getName())));
    }

    @Override
    public List<PositionDTO> add(List<PositionDTO> positions) {
        positions = positions.stream().filter(dto -> isNameNotBlank(dto.getName())).toList();
        return positions.stream().map(self::add).toList();
    }

    @Override
    public List<PositionDTO> getAll() {
        return positionRepository.findAll().stream().map(this::convert).toList();
    }

    @Override
    public PositionDTO getById(Long id) {
        return convert(getEntityById(id));
    }

    @Override
    public PositionEntity getEntityById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION, ID, id));
    }

    @Override
    public PositionDTO getByName(String name) {
        return convert(getEntityByName(name));
    }

    @Override
    public PositionEntity getEntityByName(String name) {
        isNameNotBlank(name);
        return positionRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(POSITION, NAME, name.toUpperCase(Locale.ROOT)));
    }

    @Override
    public List<PositionDTO> findAllByNameContaining(String partialName) {
        isNameNotBlank(partialName);
        return positionRepository.findAllByNameContainingIgnoreCase(partialName)
                .stream().map(e -> new PositionDTO(e.getId(), e.getName())).toList();
    }

    @Override
    @CacheEvict(value = "positions", allEntries = true)
    public PositionDTO delete(Long id) {
        return delete(getEntityById(id));
    }

    private PositionDTO delete(PositionEntity entity) {
        if (personRepository.existsByPosition_Id(entity.getId())) {
            throw new ProhibitedRemovingException(
                    ExceptionType.POSITION_IS_IN_USE.setFieldValue(entity.getName()));
        }
        positionRepository.delete(entity);
        return convert(entity);
    }

}
