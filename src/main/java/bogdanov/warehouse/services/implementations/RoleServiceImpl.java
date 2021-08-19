package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.services.interfaces.RoleService;
import bogdanov.warehouse.services.mappers.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//TODO
@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;
    private Mapper mapper;

    //region Autowired setters
    @Autowired
    private void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    private void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
    //endregion


    @Override
    public RoleDTO findByName(String name) {
        return mapper.convert(roleRepository.getByName(name));
    }

    @Override
    public List<RoleDTO> findByName(List<String> names) {
        return names.stream().map(roleRepository::getByName).map(mapper::convert).collect(Collectors.toList());
    }

    @Override
    public RoleDTO[] findByName(String[] names) {
        return findByName(Arrays.asList(names)).toArray(new RoleDTO[0]);
    }
}
