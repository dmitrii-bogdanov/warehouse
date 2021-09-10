package bogdanov.warehouse.services.implementations.cashed;


import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@RequiredArgsConstructor
@Service
@Primary
@Qualifier("repositoryWithMap")
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final Map<String, RoleEntity> entities = new HashMap<>();
    private final Map<String, RoleDTO> dto = new HashMap<>();

    @PostConstruct
    private void initializeRolesAndMap() {
        for (Role role : Role.values()) {
            try {
                roleRepository.save(new RoleEntity(role));
            } catch (Exception e) {
                continue;
            }
        }
        updateMaps();
    }

    @Override
    public List<RoleEntity> getAllEntities() {
        return entities.values().stream().sorted(Comparator.comparingLong(RoleEntity::getId)).toList();
    }

    @Override
    public RoleEntity findEntityByName(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(
                    ExceptionType.BLANK_ENTITY_NAME.setEntity(RoleEntity.class).getModifiedMessage());
        }
        name = name.toUpperCase(Locale.ROOT);
        if (dto.containsKey(name)) {
            return entities.get(name);
        }
        throw new ResourceNotFoundException("Role", "name", name);

    }

    @Override
    public List<RoleEntity> findEntitiesByName(Collection<String> names) {
        return names.stream().filter(Strings::isNotBlank).map(n -> entities.get(n.toUpperCase(Locale.ROOT))).filter(Objects::nonNull).toList();
    }

    @Override
    public List<RoleEntity> findEntitiesByName(String[] names) {
        return findEntitiesByName(Arrays.asList(names));
    }

    @Override
    public List<RoleDTO> getAll() {
        return dto.values().stream().sorted(Comparator.comparingLong(RoleDTO::getId)).toList();
    }

    @Override
    public RoleDTO findByName(String name) {
        if (Strings.isBlank(name)) {
            throw new IllegalArgumentException(
                    ExceptionType.BLANK_ENTITY_NAME.setEntity(RoleEntity.class).getModifiedMessage());
        }
        name = name.toUpperCase(Locale.ROOT);
        if (dto.containsKey(name)) {
            return dto.get(name);
        }
        throw new ResourceNotFoundException("Role", "name", name);
    }

    @Override
    public List<RoleDTO> findByName(Collection<String> names) {
        return names.stream().filter(Strings::isNotBlank).map(n -> dto.get(n.toUpperCase(Locale.ROOT))).filter(Objects::nonNull).toList();
    }

    @Override
    public List<RoleDTO> findByName(String[] names) {
        return findByName(Arrays.asList(names));
    }

    public void updateMaps() {
        roleRepository.findAll().forEach(entity -> entities.put(entity.getName(), entity));
        roleRepository.findAll()
                .forEach(entity -> dto.put(entity.getName(), new RoleDTO(entity.getId(), entity.getName())));
    }
}
