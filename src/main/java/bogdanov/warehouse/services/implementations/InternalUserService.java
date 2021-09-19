package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class InternalUserService  implements UserDetailsService {

    private final UserRepository userRepository;

    private static final String USER = "User";
    private static final String USERNAME = "username";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toUpperCase(Locale.ROOT));
    }

    public UserEntity getEntityByUsername(String username) {
        if (Strings.isBlank(username)) {
            throw new ArgumentException(ExceptionType.BLANK_USERNAME);
        }
        return userRepository.findByUsernameIgnoreCase(username.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, username.toUpperCase(Locale.ROOT)));
    }

}
