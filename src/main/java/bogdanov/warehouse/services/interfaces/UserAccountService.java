package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserAccountService extends UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserAccountDTO add(UserAccountWithPasswordDTO user);

    UserAccountDTO updatePassword(UserAccountWithPasswordDTO user);

    UserAccountDTO updateRoles(UserAccountDTO user);

    UserAccountDTO updateUsername(UserAccountDTO user);

    UserAccountDTO enable(Long id);

    UserAccountDTO disable(Long id);

    UserAccountDTO setEnabled(Long id, boolean isEnabled);

    List<UserAccountDTO> getAll();

    UserAccountDTO getById(Long id);

    UserEntity getEntityById(Long id);

    UserAccountDTO getByPersonId(Long id);

    UserAccountDTO getByUsername(String username);

    List<UserAccountDTO> findAllByRole(String role);

    UserAccountDTO delete(Long id);
}
