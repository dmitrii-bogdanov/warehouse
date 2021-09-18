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
import bogdanov.warehouse.services.interfaces.RecordService;
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
    private final RecordService recordService;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String NOT_VALID_PASSWORD_COMMENT =
            "Password is too short (min length : " + MIN_PASSWORD_LENGTH + ") or blank";

    private static final String USER = "User";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PERSON_ID = "person_id";
    private static final String ROLE = "Role";
    private static final String NAME = "name";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username.toUpperCase(Locale.ROOT));
    }

    @Override
    public UserAccountDTO add(UserAccountWithPasswordDTO user) {
        checkPassword(user.getPassword());
        UserEntity entity = mapper.convert(user);
        checkUsername(entity.getUsername());
        entity.setPerson(personService.getEntityById(user.getId()));
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
    }

    private void checkId(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
    }

    @Override
    public UserAccountDTO delete(Long id) {
        UserEntity entity = getEntityById(id);
        if (recordService.existsByUserId(id)) {
            throw new ProhibitedRemovingException(ExceptionType.USER_HAS_RECORDS.setId(id));
        } else {
            userRepository.delete(entity);
            return mapper.convert(entity, UserAccountDTO.class);
        }
    }


    @Override
    public UserAccountDTO updatePassword(UserAccountWithPasswordDTO user) {
        UserEntity entity = getEntityById(user.getId());
        checkIdAndUsername(user, entity);
        checkPassword(user.getPassword());
        entity.setPassword(mapper.convert(user).getPassword());
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
    }

    @Override
    public UserAccountDTO updateRoles(UserAccountDTO user) {
        UserEntity entity = getEntityById(user.getId());
        checkIdAndUsername(user, entity);
        entity.setRoles(mapper.convert(user).getRoles());
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
    }

    @Override
    public UserAccountDTO updateUsername(UserAccountDTO user) {
        UserEntity entity = getEntityById(user.getId());
        String newUsername = mapper.convert(user).getUsername();
        checkUsername(newUsername);
        entity.setUsername(newUsername);
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
    }

    private void checkIdAndUsername(UserAccountDTO dto, UserEntity entity) {
        if (!entity.getUsername().equalsIgnoreCase(dto.getUsername())) {
            throw new ArgumentException(ExceptionType.ID_USERNAME_INCORRECT);
        }
    }

    private void checkUsername(String username) {
        if (Strings.isBlank(username)) {
            throw new ArgumentException(ExceptionType.BLANK_USERNAME);
        }
    }

    private void checkPassword(String password) {
        if (Strings.isBlank(password) || (password.length() < MIN_PASSWORD_LENGTH)) {
            throw new ArgumentException(
                    ExceptionType.NOT_VALID_PASSWORD
                            .addComment(NOT_VALID_PASSWORD_COMMENT));
        }
    }

    @Override
    public UserAccountDTO enable(Long id) {
        return setEnabled(id, true);
    }

    @Override
    public UserAccountDTO disable(Long id) {
        return setEnabled(id, false);
    }

    private UserAccountDTO setEnabled(Long id, boolean isEnabled) {
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
    public UserEntity getEntityByUsername(String username) {
        checkUsername(username);
        return userRepository.findByUsernameIgnoreCase(StringUtils.toRootUpperCase(username))
                .orElseThrow(() -> new ResourceNotFoundException(USER, USERNAME, username));
    }

    @Override
    public UserEntity getEntityById(Long id) {
        checkId(id);
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(USER, ID, id));
    }

    @Override
    public UserAccountDTO getByPersonId(Long personId) {
        checkId(personId);
        return mapper.convert(
                userRepository.findByPerson_Id(personId)
                        .orElseThrow(() -> new ResourceNotFoundException(USER, PERSON_ID, personId)),
                UserAccountDTO.class
        );
    }

    @Override
    public UserAccountDTO getByUsername(String username) {
        return mapper.convert(getEntityByUsername(username), UserAccountDTO.class);
    }

    @Override
    public List<UserAccountDTO> findAllByRole(String role) {
        checkRoleName(role);
        return userRepository.findAllByRoles_NameEqualsIgnoreCase(role)
                .stream().map(e -> mapper.convert(e, UserAccountDTO.class)).toList();
    }

    private void checkRoleName(String roleName) {
        if (Strings.isBlank(roleName)) {
            throw new ArgumentException(ExceptionType.BLANK_ENTITY_NAME.setEntity(ROLE).setFieldName(NAME));
        }
    }

}
