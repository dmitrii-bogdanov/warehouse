package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.services.interfaces.RoleService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final Mapper mapper;

    @PostConstruct
    private void initializeRoles() {
        Arrays.stream(Role.values()).forEach(role -> roleRepository.save(new RoleEntity(role)));
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public RoleDTO findByName(String name) {
        return mapper.convert(roleRepository.getByName(name));
    }

    @Override
    public List<RoleDTO> findByName(Collection<String> names) {
        return names.stream().map(roleRepository::getByName).map(mapper::convert).collect(Collectors.toList());
    }

    @Override
    public RoleDTO[] findByName(String[] names) {
        return findByName(Arrays.asList(names)).toArray(new RoleDTO[0]);
    }
}
