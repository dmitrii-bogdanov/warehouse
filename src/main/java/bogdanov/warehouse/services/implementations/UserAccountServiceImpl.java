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
import bogdanov.warehouse.services.interfaces.RoleService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final RoleService roleService;

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String NOT_VALID_PASSWORD_COMMENT =
            "Password is too short (min length : " + MIN_PASSWORD_LENGTH + ") or blank";

    private static final String USER = "User";
    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String PERSON_ID = "person_id";
    private static final String ROLE = "Role";
    private static final String NAME = "name";
    private static final String REGEX_TO_DELETE_NESTED_EXCEPTIONS = ";.*";
    private static final String APOSTROPHE = "\"";
    private static final String SPACE_STR = " ";
    private static String DATA_INTEGRITY_EXCEPTION_SUBSTRING = "ON PUBLIC.USERS(";

    //region Util methods
    private WarehouseExeption wrapException(DataIntegrityViolationException e, UserAccountDTO dto) {
        String message = e.getMessage();
        if (message != null) {
            int index = -1;
            if ((index = message.indexOf(DATA_INTEGRITY_EXCEPTION_SUBSTRING)) > -1) {
                message = message.substring(index + DATA_INTEGRITY_EXCEPTION_SUBSTRING.length());
                ExceptionType type = null;
                if (message.startsWith(USERNAME.toUpperCase(Locale.ROOT))) {
                    type = ExceptionType.ALREADY_REGISTERED_USERNAME
                            .setFieldValue(dto.getUsername().toUpperCase(Locale.ROOT));
                } else if (message.startsWith(PERSON_ID.toUpperCase(Locale.ROOT))) {
                    type = ExceptionType.ALREADY_REGISTERED_PERSON.setId(dto.getPersonId());
                }
                if (type != null) {
                    return new ArgumentException(type);
                }
            }
        }
        throw e;
    }

    private void checkRolesNotEmpty(Collection list) {
        if (list == null || list.isEmpty()) {
            throw new ArgumentException(ExceptionType.BLANK_ENTITY_NAME.setEntity(ROLE));
        }
    }

    private void checkId(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
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

    private void checkDtoNotNull(UserAccountDTO dto) {
        if (dto == null) {
            throw new ArgumentException(ExceptionType.NO_OBJECT_WAS_PASSED);
        }
    }
    //endregion

    @Override
    public UserAccountDTO add(UserAccountWithPasswordDTO user) {
        checkDtoNotNull(user);
        checkPassword(user.getPassword());
        UserEntity entity = mapper.convert(user);
        checkUsername(entity.getUsername());
        checkRolesNotEmpty(entity.getRoles());
        entity.setPerson(personService.getEntityById(user.getPersonId()));
        try {
            return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw wrapException(e, user);
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
        checkDtoNotNull(user);
        UserEntity entity = getEntityById(user.getId());
        checkIdAndUsername(user, entity);
        checkPassword(user.getPassword());
        entity.setPassword(mapper.convert(user).getPassword());
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
    }

    @Override
    public UserAccountDTO updateRoles(UserAccountDTO user) {
        checkDtoNotNull(user);
        UserEntity entity = getEntityById(user.getId());
        checkIdAndUsername(user, entity);
        entity.setRoles(mapper.convert(user).getRoles());
        checkRolesNotEmpty(entity.getRoles());
        return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
    }

    @Override
    public UserAccountDTO updateUsername(UserAccountDTO user) {
        checkDtoNotNull(user);
        UserEntity entity = getEntityById(user.getId());
        String newUsername = mapper.convert(user).getUsername();
        checkUsername(newUsername);
        entity.setUsername(newUsername);
        try {
            return mapper.convert(userRepository.save(entity), UserAccountDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw wrapException(e, user);
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

//    @Override
//    public List<UserAccountDTO> getAllEnabled() {
//        final boolean isEnabled = true;
//        return userRepository.findAllByEnabled(isEnabled)
//                .stream().map(e -> mapper.convert(e, UserAccountDTO.class)).toList();
//    }

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
        roleService.getByName(role);
        return userRepository.findAllByRoles_NameEqualsIgnoreCase(role)
                .stream().map(e -> mapper.convert(e, UserAccountDTO.class)).toList();
    }

}
