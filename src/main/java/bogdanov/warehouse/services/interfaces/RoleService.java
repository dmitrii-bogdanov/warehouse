package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.dto.RoleDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface RoleService {

    List<RoleDTO> getAll();

    RoleDTO getByName(String name);

    List<RoleEntity> getAllEntities();

    RoleEntity getEntityByName(String name);

}
