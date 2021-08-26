package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.dto.UserDTO;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService{

    List<UserDTO> getAll();

    UserDTO getById(Long id);

    UserDTO getByUsername(String username);

    UserDTO getByPersonId(Long id);

    List<UserDTO> findAllByFirstname(String firstname);

    List<UserDTO> findAllByLastname(String lastname);

    List<UserDTO> findAllByPatronymic(String patronymic);

    List<UserDTO> findAllByFullName(String firstname, String patronymic, String lastname);

    List<UserDTO> findAllByPosition(String position);

}
