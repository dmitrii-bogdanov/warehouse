package bogdanov.warehouse.configs;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.PositionRepository;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.services.mappers.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.yaml.snakeyaml.external.com.google.gdata.util.common.base.PercentEscaper;

import java.time.LocalDate;

@Configuration
//@PropertySource("classpath:admin.properties")
public class RolesConfig {

    @Autowired
    private void initializeRoles(RoleRepository roleRepository) {
        for (Role role : Role.values()) {
            roleRepository.save(new RoleEntity(role.getId(), role.name()));
        }
    }

}
