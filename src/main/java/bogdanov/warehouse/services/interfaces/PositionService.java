package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.dto.PositionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PositionService {

    PositionDTO add(String name);

    PositionDTO add(PositionDTO position);

    List<PositionDTO> add(List<PositionDTO> positions);

    PositionDTO update(PositionDTO position);

    List<PositionDTO> update(List<PositionDTO> positions);

    List<PositionDTO> getAll();

    PositionDTO getById(Long id);

    PositionDTO getByName(String name);

    List<PositionDTO> findAllByNameContaining(String partialName);

}
