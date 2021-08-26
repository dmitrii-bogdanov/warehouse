package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.dto.RoleDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface RoleService {

    public List<RoleDTO> getAll();

    public RoleDTO findByName(String name);

    public List<RoleDTO> findByName(Collection<String> names);

    public RoleDTO[] findByName(String[] names);

}
