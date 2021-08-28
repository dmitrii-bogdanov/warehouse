package bogdanov.warehouse.configs;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.services.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;

@Configuration
public class AdminAccountConfig {

    @Autowired
    private void initializeAdmin(@Qualifier("admin") BCryptPasswordEncoder encoder,
                                 UserRepository userRepository,
                                 RoleService roleService) {
        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPassword(encoder.encode("password"));
        admin.getRoles().add(roleService.findEntityByName("ROLE_ADMIN"));
        admin.setEnabled(true);
        userRepository.save(admin);
    }

}
