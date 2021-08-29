package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.RoleService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private int minPasswordLength = 8;
    private final PersonService personService;
    private final RecordRepository recordRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toUpperCase(Locale.ROOT));
    }

    @Override
    public UserAccountDTO add(UserAccountWithPasswordDTO user) {
        if (isUsernameAvailable(user.getUsername()) && isPasswordValid(user.getPassword())) {
            PersonEntity person = personService.getEntityById(user.getPersonId());
            if (userRepository.existsByPersonEquals(person)) {
                throw new AlreadyRegisteredPersonException(
                        "Person with id : " + user.getPersonId() + " has already been registered");
            } else {
                UserEntity entity = mapper.convert(user);
                entity.setPerson(person);
                return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
            }
        }
        throw new RuntimeException("Some unforeseen exception in UserAccountServiceImpl.add()!");
    }

    @Override
    public UserAccountDTO delete(Long id) {
        UserEntity entity = getEntityById(id);
        if (recordRepository.existsByUser_Id(id)) {
            throw new ProhibitedRemovingException("User has records");
        } else {
            userRepository.delete(entity);
            return mapper.convert(entity, UserAccountDTO.class);
        }
    }

    @Override
    public UserAccountDTO updatePassword(UserAccountWithPasswordDTO user) {
        if (user.getId() == null) {
            throw new NullIdException();
        }
        UserEntity entity = getEntityById(user.getId());
        if (isIdAndUsernameCorrect(user, entity)) {
            entity.setPassword(mapper.convert(user).getPassword());
            return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
        }
        throw new RuntimeException("Some unforeseen exception in UserAccountServiceImpl.updatePassword()!");
    }

    @Override
    public UserAccountDTO updateRoles(UserAccountDTO user) {
        UserEntity entity = getEntityById(user.getId());
        if (isIdAndUsernameCorrect(user, entity)) {
            entity.setRoles(mapper.convert(user).getRoles());
            return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
        }
        throw new RuntimeException("Some unforeseen exception in UserAccountServiceImpl.updateRoles()!");
    }

    @Override
    public UserAccountDTO updateUsername(UserAccountDTO user) {
        UserEntity entity = getEntityById(user.getId());
        String newUsername = user.getUsername();
        if (!entity.getUsername().equals(newUsername) && isUsernameAvailable(newUsername)) {
            entity.setUsername(newUsername);
            return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
        }
        return mapper.convert(entity, UserAccountDTO.class);
    }

    private boolean isIdAndUsernameCorrect(UserAccountDTO dto, UserEntity entity) {
        if (entity.getUsername().equals(dto.getUsername())) {
            return true;
        } else {
            throw new UsernameException("User id/username is incorrect");
        }
    }

    private boolean isUsernameAvailable(String username) {
        if (Strings.isBlank(username) || userRepository.existsByUsername(username)) {
            throw new UsernameException("Username is already registered or blank");
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (Strings.isBlank(password) || (password.length() < minPasswordLength)) {
            throw new PasswordException(
                    "Password is too short (min length : " + minPasswordLength + ") or blank");
        }
        return true;
    }

    @Override
    public UserAccountDTO enable(Long id) {
        return setEnabled(id, true);
    }

    @Override
    public UserAccountDTO disable(Long id) {
        return setEnabled(id, false);
    }

    @Override
    public UserAccountDTO setEnabled(Long id, boolean isEnabled) {
        if (id == null) {
            throw new NullIdException();
        }
        UserEntity entity = getEntityById(id);
        entity.setEnabled(isEnabled);
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);

    }

    @Override
    public List<UserAccountDTO> getAll() {
        return userRepository.findAll().stream().map(e -> mapper.convert(e, UserAccountDTO.class)).toList();
    }

    @Override
    public UserAccountDTO getById(Long id) {
        return mapper.convert(getEntityById(id), UserAccountDTO.class);
    }

    @Override
    public UserEntity getEntityById(Long id) {
        if (id == null) {
            throw new NullIdException();
        }
        Optional<UserEntity> optionalEntity = userRepository.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get();
        } else {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    @Override
    public UserAccountDTO getByPersonId(Long personId) {
        if (personId == null) {
            throw new NullIdException();
        }
        Optional<UserEntity> optionalEntity = userRepository.findByPerson_Id(personId);
        if (optionalEntity.isPresent()) {
            return mapper.convert(optionalEntity.get(), UserAccountDTO.class);
        } else {
            throw new ResourceNotFoundException("Person", "id", personId);
        }
    }

    @Override
    public UserAccountDTO getByUsername(String username) {
        username = username.toUpperCase(Locale.ROOT);
        UserEntity entity = userRepository.findByUsername(username);
        if (entity != null) {
            return mapper.convert(entity, UserAccountDTO.class);
        } else {
            throw new ResourceNotFoundException("User", "username", username);
        }
    }

    @Override
    public List<UserAccountDTO> findAllByRole(String role) {
        return userRepository.findAllByRoles_NameEquals(role)
                .stream().map(e -> mapper.convert(e, UserAccountDTO.class)).toList();
    }

}
