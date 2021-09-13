package bogdanov.warehouse.services.implementations.cashed;


import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
@Primary
@Qualifier("cached")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    private static final String ROLE = "Role";
    private static final String NAME = "name";

    private RoleDTO convert(RoleEntity entity) {
        return objectMapper.convertValue(entity, RoleDTO.class);
    }

    @Override
    public List<RoleEntity> getAllEntities() {
        return roleRepository.findAll();
    }

    @Override
    public List<RoleDTO> getAll() {
        return getAllEntities().stream().map(this::convert).toList();
    }

    @Override
    @Cacheable("roles")
    public RoleEntity getEntityByName(String name) {
        if (Strings.isBlank(name)) {
            throw new ArgumentException(ExceptionType.BLANK_ENTITY_NAME.setEntity(ROLE));
        }
        return roleRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE, NAME, name.toUpperCase(Locale.ROOT)));
    }

    @Override
    public RoleDTO getByName(String name) {
        return convert(getEntityByName(name));
    }

}
