package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.enums.Role;
import bogdanov.warehouse.dto.UserRegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class UserRegistrationMapper {

    private BCryptPasswordEncoder userEncoder;
    private BCryptPasswordEncoder adminEncoder;

    @Autowired
    private void setEncoders(@Qualifier("user") BCryptPasswordEncoder userEncoder,
                             @Qualifier("admin") BCryptPasswordEncoder adminEncoder) {
        this.userEncoder = this.userEncoder;
        this.adminEncoder = adminEncoder;
    }
//
//    UserEntity convert(UserRegistrationDTO user) {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setId(user.getId());
//        userEntity.setUsername(user.getUsername());
//        boolean isAdmin = false;
//        for (String role : user.getRoles()) {
//            isAdmin |= role.toUpperCase(Locale.ROOT).equals(Role.ROLE_ADMIN.name());
//
//        }
//        if (!isAdmin) {
//            userEntity.setPassword(userEncoder.encode(user.getPassword()));
//        }
//        if (staff)
//    }

}
