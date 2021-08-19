package bogdanov.warehouse.configs;

import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.RoleRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@PropertySource("classpath:admin.properties")
public class RolesConfig {

    @Autowired
    private void initializeRoles(RoleRepository roleRepository) {
        for (Role role : Role.values()) {
            roleRepository.save(new RoleEntity(role));
        }
    }

    @Autowired
    private void initializeDefaultAdmin(UserRepository userRepository,
                                        @Qualifier("admin") BCryptPasswordEncoder encoder,
                                        @Value("${admin.username}") String username,
                                        @Value("${admin.password}") String password) {
        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setPassword(encoder.encode(password));
        admin.getRoles().add(new RoleEntity(Role.ROLE_ADMIN));
        admin.getRoles().add(new RoleEntity(Role.ROLE_STAFF));
        admin.getRoles().add(new RoleEntity(Role.ROLE_USER));
        userRepository.save(admin);
    }

    //TODO DELETE
    @Autowired
    private void initializeDefaultStaff(UserRepository userRepository,
                                        @Qualifier("user") BCryptPasswordEncoder encoder,
                                        @Value("staff") String username,
                                        @Value("password") String password) {
        UserEntity staff = new UserEntity();
        staff.setUsername(username);
        staff.setPassword(encoder.encode(password));
        staff.getRoles().add(new RoleEntity(Role.ROLE_STAFF));
        staff.getRoles().add(new RoleEntity(Role.ROLE_USER));
        userRepository.save(staff);
    }

    //TODO DELETE
    @Autowired
    private void initializeDefaultUser(UserRepository userRepository,
                                        @Qualifier("user") BCryptPasswordEncoder encoder,
                                        @Value("user") String username,
                                        @Value("password") String password) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.getRoles().add(new RoleEntity(Role.ROLE_STAFF));
        user.getRoles().add(new RoleEntity(Role.ROLE_USER));
        userRepository.save(user);
    }


}
