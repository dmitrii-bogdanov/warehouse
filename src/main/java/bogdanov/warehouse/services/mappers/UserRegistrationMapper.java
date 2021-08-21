package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Locale;

@Component
public class UserRegistrationMapper {

    private final BCryptPasswordEncoder userEncoder;
    private final BCryptPasswordEncoder adminEncoder;

    UserRegistrationMapper(@Qualifier("user") BCryptPasswordEncoder userEncoder,
                           @Qualifier("admin") BCryptPasswordEncoder adminEncoder) {
        this.userEncoder = userEncoder;
        this.adminEncoder = adminEncoder;
    }

    //TODO CHECK WARNINGS
    UserEntity convert(UserAccountWithPasswordDTO user) {
        UserEntity userEntity = convert((UserAccountDTO) user);
        if (userEntity.getRoles().contains(new RoleEntity(Role.ROLE_ADMIN))) {
            userEntity.setPassword(adminEncoder.encode(user.getPassword()));
        } else {
            userEntity.setPassword(userEncoder.encode(user.getPassword()));
        }
        return userEntity;
    }

    //TODO Check adding by new RoleEntity()
    UserEntity convert(UserAccountDTO user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setUsername(user.getUsername());
        Collection<RoleEntity> roles = userEntity.getRoles();
        for (String role : user.getRoles()) {
            try {
                roles.add(new RoleEntity(
                        Role.valueOf(role.toUpperCase(Locale.ROOT))
                ));
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        return userEntity;
    }

    UserAccountDTO convert(UserEntity user) {
        UserAccountDTO regInfo = new UserAccountWithPasswordDTO();
        regInfo.setId(user.getId());
        regInfo.setUsername(user.getUsername());
        regInfo.setPersonId(user.getPerson().getId());
        String[] roles = new String[user.getRoles().size()];
        int i = 0;
        for (RoleEntity role : user.getRoles()) {
            roles[i] = role.getName();
        }
        return regInfo;
    }

}
