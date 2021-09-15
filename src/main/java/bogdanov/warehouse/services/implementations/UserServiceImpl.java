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
    private final PersonService personService;
    private final UserAccountService accountService;

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public UserDTO getById(Long id) {
        Optional<UserEntity> entity = userRepository.findById(id);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
        } else {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    @Override
    public UserDTO getByUsername(String username) {
        return mapper.convert(accountService.getEntityByUsername(username));
    }

    @Override
    public UserDTO getByPersonId(Long personId) {
        Optional<UserEntity> entity = userRepository.findByPerson_Id(personId);
        if (entity.isPresent()) {
            return mapper.convert(entity.get());
        } else {
            throw new ResourceNotFoundException("Person", "id", personId);
        }
    }

    @Override
    public List<UserDTO> findAllByFirstname(String firstname) {
        return userRepository.findAllByPerson_Firstname(firstname)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<UserDTO> findAllByLastname(String lastname) {
        return userRepository.findAllByPerson_Lastname(lastname)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<UserDTO> findAllByPatronymic(String patronymic) {
        return userRepository.findAllByPerson_Patronymic(patronymic)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<UserDTO> findAllByFullName(String firstname, String patronymic, String lastname) {
        return userRepository
                .findAllByPerson_FirstnameAndPerson_PatronymicAndPerson_Lastname(firstname, patronymic, lastname)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<UserDTO> findAllByPosition(String position) {
        return userRepository.findAllByPerson_Position_Name(position)
                .stream().map(mapper::convert).toList();
    }

}
