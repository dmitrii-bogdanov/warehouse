package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    UserDTO convert(UserEntity user) {
        return user.getPerson() == null
                ? new UserDTO(user.getId(), user.getUsername(), null)
                : new UserDTO(user.getId(), user.getUsername(), user.getPerson().getId());
    }

    UserDTO convert(UserAccountDTO account) {
        return new UserDTO(account.getId(), account.getUsername(), account.getPersonId());
    }

}
