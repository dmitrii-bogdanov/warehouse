package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.BlankNameException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.PositionService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final Map<String, PositionDTO> positions = new HashMap<>();

    @PostConstruct
    private void initializeMap() {
        positionRepository.findAll().forEach(e -> positions.put(e.getName(), new PositionDTO(e.getId(), e.getName())));
    }

    @Override
    public PositionDTO add(String name) {
        positions.computeIfAbsent(name, n -> {
            PositionEntity entity = positionRepository.save(new PositionEntity(name));
            return new PositionDTO(entity.getId(), entity.getName());
        });
        return positions.get(name);
    }

    @Override
    public PositionDTO add(PositionDTO position) {
        return add(position.getName());
    }

    @Override
    public List<PositionDTO> add(List<PositionDTO> positions) {
        return positions.stream().map(this::add).toList();
    }

    //TODO make with unique
    @Override
    public PositionDTO update(PositionDTO position) {
        return null;
    }

    @Override
    public List<PositionDTO> update(List<PositionDTO> positions) {
        return positions.stream().map(this::update).toList();
    }

    //TODO sort
    @Override
    public List<PositionDTO> getAll() {
        return positions.values().stream().toList();
    }

    //TODO
    @Override
    public PositionDTO getById(Long id) {
        return null;
    }

    @Override
    public PositionDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new BlankNameException("Name is blank");
        }
        if (positions.containsKey(name)) {
            return positions.get(name);
        } else {
            throw new ResourceNotFoundException("Position with name : " + name + " not found");
        }
    }

    @Override
    public List<PositionDTO> findAllByNameContaining(String partialName) {
        return positions.keySet().stream().filter(name -> name.contains(partialName)).map(positions::get).toList();
    }
}
