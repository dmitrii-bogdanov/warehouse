package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.interfaces.UserService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final UserAccountService accountService;

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public UserDTO getById(Long id) {
        return mapper.convert(accountService.getEntityById(id));
    }

    @Override
    public UserDTO getByUsername(String username) {
        return mapper.convert(accountService.getEntityByUsername(username));
    }

    @Override
    public UserDTO getByPersonId(Long personId) {
        return mapper.convert(accountService.getByPersonId(personId), UserDTO.class);
    }

}
