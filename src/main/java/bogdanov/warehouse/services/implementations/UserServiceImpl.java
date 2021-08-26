package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDTO> getAll() {
        return null;
    }

    @Override
    public UserDTO getById(Long id) {
        return null;
    }

    @Override
    public UserDTO getByUsername(String username) {
        return null;
    }

    @Override
    public UserDTO findAllByRole(String role) {
        return null;
    }

    @Override
    public UserDTO getByPersonId(Long id) {
        return null;
    }

    @Override
    public List<UserDTO> findAllByFirstname(String firstname) {
        return null;
    }

    @Override
    public List<UserDTO> findAllByLastname(String lastname) {
        return null;
    }

    @Override
    public List<UserDTO> findAllByPatronymic(String patronymic) {
        return null;
    }
}
