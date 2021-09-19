package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.services.interfaces.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
public class UserAccountMapper {

    private final BCryptPasswordEncoder userEncoder;
    private final BCryptPasswordEncoder adminEncoder;
    private final RoleService roleService;

    private final RoleEntity ROLE_ADMIN = new RoleEntity(Role.ROLE_ADMIN);

    public UserAccountMapper(@Qualifier("user") BCryptPasswordEncoder userEncoder,
                             @Qualifier("admin") BCryptPasswordEncoder adminEncoder,
                             RoleService roleService) {
        this.userEncoder = userEncoder;
        this.adminEncoder = adminEncoder;
        this.roleService = roleService;
    }

    private String toUpperCase(String str) {
        return Strings.isBlank(str) ? null : str.toUpperCase(Locale.ROOT);
    }

    UserEntity convert(UserAccountWithPasswordDTO user) {
        UserEntity userEntity = convert((UserAccountDTO) user);
        if (userEntity.getRoles().contains(ROLE_ADMIN)) {
            userEntity.setPassword(adminEncoder.encode(user.getPassword()));
        } else {
            userEntity.setPassword(userEncoder.encode(user.getPassword()));
        }
        return userEntity;
    }

    UserEntity convert(UserAccountDTO user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setUsername(toUpperCase(user.getUsername()));
        if (user.getRoles() != null) {
            userEntity.getRoles().addAll(user.getRoles().stream().map(roleService::getEntityByName).toList());
        }
        return userEntity;
    }

    UserAccountDTO convert(UserEntity user) {
        return new UserAccountDTO(
                user.getId(),
                user.getUsername(),
                user.getPerson() == null ? null : user.getPerson().getId(),
                user.getRoles().stream().map(RoleEntity::getName).toList(),
                user.isEnabled()
        );
    }

}
