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
            roleRepository.save(new RoleEntity(role.getId(), role.name()));
        }
    }

    @Autowired
    private void initializeDefaultAdmin(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        @Qualifier("admin") BCryptPasswordEncoder encoder,
                                        @Value("${admin.username}") String username,
                                        @Value("${admin.password}") String password) {
        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setPassword(encoder.encode(password));
        admin.getRoles().add(roleRepository.getByName("ROLE_ADMIN"));
        admin.getRoles().add(roleRepository.getByName("ROLE_STAFF"));
        admin.getRoles().add(roleRepository.getByName("ROLE_USER"));
        admin.getRoles().add(roleRepository.getByName("ROLE_EXTERIOR_MANAGER"));
        admin.getRoles().add(roleRepository.getByName("ROLE_MANAGER"));
        userRepository.save(admin);
    }

    //TODO DELETE
    @Autowired
    private void initializeDefaultStaff(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        @Qualifier("user") BCryptPasswordEncoder encoder,
                                        @Value("staff") String username,
                                        @Value("password") String password) {
        UserEntity staff = new UserEntity();
        staff.setUsername(username);
        staff.setPassword(encoder.encode(password));
        staff.getRoles().add(roleRepository.getByName("ROLE_STAFF"));
        staff.getRoles().add(roleRepository.getByName("ROLE_USER"));
        userRepository.save(staff);
    }

    //TODO DELETE
    @Autowired
    private void initializeDefaultUser(UserRepository userRepository,
                                       RoleRepository roleRepository,
                                       @Qualifier("user") BCryptPasswordEncoder encoder,
                                       @Value("user") String username,
                                       @Value("password") String password) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.getRoles().add(roleRepository.getByName("ROLE_USER"));
        userRepository.save(user);
    }

    //TODO DELETE
    @Autowired
    private void initializeDefaultManager(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        @Qualifier("user") BCryptPasswordEncoder encoder,
                                        @Value("manager") String username,
                                        @Value("password") String password) {
        UserEntity staff = new UserEntity();
        staff.setUsername(username);
        staff.setPassword(encoder.encode(password));
        staff.getRoles().add(roleRepository.getByName("ROLE_STAFF"));
        staff.getRoles().add(roleRepository.getByName("ROLE_USER"));
        staff.getRoles().add(roleRepository.getByName("ROLE_MANAGER"));
        userRepository.save(staff);
    }

    //TODO DELETE
    @Autowired
    private void initializeDefaultExteriorManager(UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        @Qualifier("user") BCryptPasswordEncoder encoder,
                                        @Value("extmanager") String username,
                                        @Value("password") String password) {
        UserEntity staff = new UserEntity();
        staff.setUsername(username);
        staff.setPassword(encoder.encode(password));
        staff.getRoles().add(roleRepository.getByName("ROLE_STAFF"));
        staff.getRoles().add(roleRepository.getByName("ROLE_USER"));
        staff.getRoles().add(roleRepository.getByName("ROLE_EXTERIOR_MANAGER"));
        userRepository.save(staff);
    }

}
