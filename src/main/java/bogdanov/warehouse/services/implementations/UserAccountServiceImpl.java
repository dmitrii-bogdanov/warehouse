package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.database.repositories.RecordRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Primary
@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    private final PersonService personService;
    private final RecordRepository recordRepository;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String NOT_VALID_PASSWORD_COMMENT =
            "Password is too short (min length : " + MIN_PASSWORD_LENGTH + ") or blank";

    private static final String USER = "User";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PERSON_ID = "person_id";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toUpperCase(Locale.ROOT));
    }

    @Override
    public UserAccountDTO add(UserAccountWithPasswordDTO user) {
        if (isUsernameAvailable(user.getUsername()) && isPasswordValid(user.getPassword())) {
            PersonEntity person = personService.getEntityById(user.getPersonId());
            if (userRepository.existsByPersonEquals(person)) {
                throw new IllegalArgumentException(
                        ExceptionType.ALREADY_REGISTERED_PERSON.setId(user.getPersonId()).getModifiedMessage());
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
            throw new ProhibitedRemovingException(ExceptionType.USER_HAS_RECORDS.setId(id));
        } else {
            userRepository.delete(entity);
            return mapper.convert(entity, UserAccountDTO.class);
        }
    }


    @Override
    public UserAccountDTO updatePassword(UserAccountWithPasswordDTO user) {
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
            throw new IllegalArgumentException(ExceptionType.ID_USERNAME_INCORRECT.getMessage());
        }
    }

    private boolean isUsernameAvailable(String username) {
        if (Strings.isBlank(username) || userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(ExceptionType.ALREADY_REGISTERED_OR_BLANK_USERNAME.getMessage());
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        if (Strings.isBlank(password) || (password.length() < MIN_PASSWORD_LENGTH)) {
            throw new ArgumentException(
                    ExceptionType.NOT_VALID_PASSWORD
                            .addComment(NOT_VALID_PASSWORD_COMMENT));
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
    @Cacheable("users")
    public UserEntity getEntityByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(StringUtils.toRootUpperCase(username)))
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, username));
    }

    @Override
    public UserEntity getEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(USER, ID, id));
    }

    @Override
    public UserAccountDTO getByPersonId(Long personId) {
        Optional<UserEntity> optionalEntity = userRepository.findByPerson_Id(personId);
        if (optionalEntity.isPresent()) {
            return mapper.convert(optionalEntity.get(), UserAccountDTO.class);
        } else {
            throw new ResourceNotFoundException(USER, PERSON_ID, personId);
        }
    }

    @Override
    public UserAccountDTO getByUsername(String username) {
        username = username.toUpperCase(Locale.ROOT);
        UserEntity entity = userRepository.findByUsername(username);
        if (entity != null) {
            return mapper.convert(entity, UserAccountDTO.class);
        } else {
            throw new ResourceNotFoundException(USER, USERNAME, username);
        }
    }

    @Override
    public List<UserAccountDTO> findAllByRole(String role) {
        return userRepository.findAllByRoles_NameEquals(role)
                .stream().map(e -> mapper.convert(e, UserAccountDTO.class)).toList();
    }

    //TODO delete (for test)
    @Override
    public UserEntity getByIdWithoutNullCheck(Long id) {
        return userRepository.getById(id);
    }

}
