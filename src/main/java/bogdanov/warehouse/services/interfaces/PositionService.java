package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.dto.PositionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PositionService {

    PositionEntity add(String name);

    PositionDTO add(PositionDTO position);

    List<PositionDTO> add(List<PositionDTO> positions);

    List<PositionDTO> getAll();

    PositionDTO getById(Long id);

    PositionDTO getByName(String name);

    PositionEntity getEntityById(Long id);

    PositionEntity getEntityByName(String name);

    List<PositionDTO> findAllByNameContaining(String partialName);

    PositionDTO delete(String name);

    PositionDTO delete(Long id);

}
