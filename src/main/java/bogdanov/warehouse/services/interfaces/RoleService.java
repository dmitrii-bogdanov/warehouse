package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.dto.RoleDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface RoleService {

    List<RoleDTO> getAll();

    RoleDTO findByName(String name);

    List<RoleDTO> findByName(Collection<String> names);

    List<RoleDTO> findByName(String[] names);

    List<RoleEntity> getAllEntities();

    RoleEntity findEntityByName(String name);

    List<RoleEntity> findEntitiesByName(Collection<String> names);

    List<RoleEntity> findEntitiesByName(String[] names);

}
