package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.BlankNameException;
import bogdanov.warehouse.exceptions.NullIdException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final Mapper mapper;

    @Override
    public PositionDTO add(PositionDTO position) {
        return mapper.convert(
                positionRepository.findByName(position.getName())
                        .orElse(positionRepository.save(mapper.convert(position)))
        );
    }

    @Override
    public List<PositionDTO> add(List<PositionDTO> positions) {
        return positions.stream().map(this::add).toList();
    }

    @Override
    public PositionDTO update(PositionDTO position) {
        Optional<PositionEntity> optionalEntity = positionRepository.findById(position.getId());
        if (optionalEntity.isPresent()) {
                PositionEntity entity = optionalEntity.get();
                optionalEntity = positionRepository.findByName(position.getName());
                if (optionalEntity.isPresent()) {
                    positionRepository.delete(entity);
                    return mapper.convert(optionalEntity.get());
                } else {
                    entity.setName(position.getName());
                    return mapper.convert(positionRepository.save(entity));
                }
        } else {
            throw new ResourceNotFoundException("Position with id : " + position.getId() + " not found");
        }
    }

    @Override
    public List<PositionDTO> update(List<PositionDTO> positions) {
        return positions.stream().map(this::update).toList();
    }

    @Override
    public List<PositionDTO> getAll() {
        return positionRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public PositionDTO getById(Long id) {
        if (id == null) {
            throw new NullIdException("Id is null");
        }
        Optional<PositionEntity> optionalEntity = positionRepository.findById(id);
        if (optionalEntity.isPresent()) {
            return mapper.convert(optionalEntity.get());
        } else {
            throw new ResourceNotFoundException("Position with id : " + id + " not found");
        }
    }

    @Override
    public PositionDTO getByName(String name) {
        if (Strings.isBlank(name)) {
            throw new BlankNameException("Name is blank");
        }
        Optional<PositionEntity> optionalEntity = positionRepository.findByName(name);
        if (optionalEntity.isPresent()) {
            return mapper.convert(optionalEntity.get());
        } else {
            throw new ResourceNotFoundException("Position with name : " + name + " not found");
        }
    }

    @Override
    public List<PositionDTO> findAllByNameContaining(String partialName) {
        return positionRepository.findAllByNameContaining(partialName).stream().map(mapper::convert).toList();
    }
}
