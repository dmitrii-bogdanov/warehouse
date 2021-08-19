package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.dto.RoleDTO;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO
@Service
public interface RoleService {

    public RoleDTO findByName(String name);

    public List<RoleDTO> findByName(List<String> names);

    public RoleDTO[] findByName(String[] names);

}
