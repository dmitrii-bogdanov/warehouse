package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.interfaces.UserService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//TODO
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final UserAccountService accountService;

    private static final String USER = "User";
    private static final String ID = "id";
    private static final String PERSON = "Person";

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
        return mapper.convert(userRepository.findByPerson_Id(personId)
                .orElseThrow(() -> new ResourceNotFoundException(PERSON, ID, personId)));
    }

    @Override
    public List<UserDTO> findAllByFullName(String firstname, String patronymic, String lastname) {
        return userRepository
                .findAllByPerson_FirstnameIgnoreCaseAndPerson_PatronymicIgnoreCaseAndPerson_LastnameIgnoreCase(
                        firstname, patronymic, lastname)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<UserDTO> findAllByPosition(String position) {
        return userRepository.findAllByPerson_Position_NameIgnoreCase(position)
                .stream().map(mapper::convert).toList();
    }

}
