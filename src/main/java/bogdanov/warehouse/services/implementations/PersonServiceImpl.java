package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PositionService positionService;

    private static final String PERSON = "Person";
    private static final String ID = "id";
    private static final String RESERVED_NULL_PATRONYMIC = "NULL";
    private static final String PATRONYMIC = "patronymic";
    private static final String SEARCH_POSITION_DELIMITER = ",";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String POSITION = "Position";
    private static final String EXISTING_REFERENCE_SUBSTRING = "REFERENCES PUBLIC.PERSON";
    private static final String USER_FOREIGN_KEY_SUBSTRING = "PUBLIC.USER_ENTITY FOREIGN KEY(PERSON";


    //region Util methods
    private boolean areAllRequiredFieldsPresent(PersonDTO dto) {
        if (Strings.isNotBlank(dto.getFirstname())
                && Strings.isNotBlank(dto.getLastname())
                && dto.getBirth() != null
                && Strings.isNotBlank(dto.getPosition())) {
            return true;
        } else {
            throw new ArgumentException(ExceptionType.NOT_ALL_PERSON_REQUIRED_FIELDS);
        }
    }

    private boolean areValuesNotReserved(PersonDTO dto) {
        if (RESERVED_NULL_PATRONYMIC.equalsIgnoreCase(dto.getPatronymic())) {
            throw new ArgumentException(
                    ExceptionType.RESERVED_VALUE
                            .setFieldName(PATRONYMIC)
                            .setFieldValue("\"" + RESERVED_NULL_PATRONYMIC + "\""));
        }
        return true;
    }

    private boolean isIdNotNull(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
        return true;
    }

    private boolean isNameNotBlank(String name) {
        if (Strings.isBlank(name)) {
            throw new ArgumentException(ExceptionType.BLANK_NAME);
        }
        return true;
    }

    private RuntimeException wrapException(DataIntegrityViolationException e, PersonEntity entity) {
        String message = e.getMessage();
        message = message == null ? Strings.EMPTY : message;
        if (message.contains(EXISTING_REFERENCE_SUBSTRING)) {
            if (message.contains(USER_FOREIGN_KEY_SUBSTRING)) {
                return new ProhibitedRemovingException(ExceptionType.ALREADY_REGISTERED_PERSON.setId(entity.getId()));
            }
        }
        return e;
    }

    private boolean isListNotEmpty(Collection<PersonDTO> collection) {
        if (collection.isEmpty()) {
            throw new ArgumentException(ExceptionType.NO_OBJECT_WAS_PASSED);
        }
        return true;
    }
    //endregion

    @Override
    public List<PersonDTO> add(List<PersonDTO> persons) {
        isListNotEmpty(persons);
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(this::areAllRequiredFieldsPresent)
                .filter(this::areValuesNotReserved)
                .map(mapper::convert)
                .peek(e -> e.setId(null)) //TODO check alternative
                .toList();
        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> update(List<PersonDTO> persons) {
        isListNotEmpty(persons);
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(this::areAllRequiredFieldsPresent)
                .filter(this::areValuesNotReserved)
                .map(mapper::convert)
                .toList();

        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public PersonDTO delete(Long id) {
        PersonEntity entity = getEntityById(id);
        if (userRepository.existsByPerson_Id(id)) {
            throw new ProhibitedRemovingException(ExceptionType.ALREADY_REGISTERED_PERSON.setId(id));
        } else {
            try {
                personRepository.delete(entity);
            } catch (DataIntegrityViolationException e) {
                throw wrapException(e, entity);
            }
            return mapper.convert(entity);
        }
    }

    @Override
    public PersonDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public PersonEntity getEntityById(Long id) {
        Optional<PersonEntity> optionalEntity = personRepository.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get();
        } else {
            throw new ResourceNotFoundException(PERSON, ID, id);
        }
    }

    @Override
    public List<PersonDTO> getAll() {
        return personRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> search(String firstname, String lastname, String patronymic,
                                  String position, String phoneNumber, String email,
                                  LocalDate fromDate, LocalDate toDate) {
        boolean isFirstnameBlank = Strings.isBlank(firstname);
        boolean isLastnameBlank = Strings.isBlank(lastname);
        boolean isPositionBlank = Strings.isBlank(position);
        boolean isPhoneNumberBlank = Strings.isBlank(phoneNumber);
        boolean isPatronymicBlank = Strings.isBlank(patronymic);
        boolean isEmailBlank = Strings.isBlank(email);
        boolean isFromDateAbsent = fromDate == null;
        boolean isToDateAbsent = toDate == null;
        List<PositionEntity> positions = new LinkedList<>();
        List<PersonEntity> entities;

        boolean shouldPatronymicBeNull = RESERVED_NULL_PATRONYMIC.equalsIgnoreCase(patronymic);

        if (isFirstnameBlank && isLastnameBlank && isPatronymicBlank
                && isPhoneNumberBlank && isEmailBlank
                && isFromDateAbsent && isToDateAbsent) {
            throw new ArgumentException(ExceptionType.NO_PARAMETER_IS_PRESENT);
        }


        if (isFirstnameBlank) {
            firstname = Strings.EMPTY;
        }
        if (isLastnameBlank) {
            lastname = Strings.EMPTY;
        }
        if (isPatronymicBlank) {
            patronymic = Strings.EMPTY;
        }
        if (isPhoneNumberBlank) {
            phoneNumber = Strings.EMPTY;
        }
        if (isEmailBlank) {
            email = Strings.EMPTY;
        }
        if (isFromDateAbsent) {
            fromDate = LocalDate.MIN;
        }
        if (isToDateAbsent) {
            toDate = LocalDate.MAX;
        }
        if (fromDate.compareTo(toDate) > 0) {
            throw new ArgumentException(ExceptionType.INCORRECT_RANGE.setFrom(FROM_DATE).setTo(TO_DATE));
        }
        if (!isPositionBlank) {
            positions = getPositionEntitiesFromString(position);
        }

        if (isPositionBlank) {
            entities = shouldPatronymicBeNull
                    //patronymic is null, any position
                    ? searchWithNullPatronymicAndAnyPosition(firstname, lastname, phoneNumber, email, fromDate, toDate)
                    //any position
                    : searchWithAnyPosition(firstname, lastname, patronymic, phoneNumber, email, fromDate, toDate);
        } else {
            entities = shouldPatronymicBeNull
                    //patronymic is null
                    ? searchWithNullPatronymic(firstname, lastname, positions, phoneNumber, email, fromDate, toDate)
                    //full
                    : searchFull(firstname, lastname, patronymic, positions, phoneNumber, email, fromDate, toDate);
        }

        return entities.stream().map(mapper::convert).toList();
    }

    //region Search util methods

    private List<PositionEntity> getPositionEntitiesFromString(String positions) {
        return Arrays.stream(positions.split(SEARCH_POSITION_DELIMITER))
                .filter(Strings::isNotBlank)
                .map(Long::parseLong)
                .map(positionService::getEntityById)
                .toList();
    }

    //patronymic is null, any position
    private List<PersonEntity> searchWithNullPatronymicAndAnyPosition(String firstname, String lastname,
                                                                      String phoneNumber, String email,
                                                                      LocalDate startDate, LocalDate endDate) {
        return personRepository.findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicIsNullAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                firstname, lastname, phoneNumber, email, startDate, endDate);
    }

    //any position
    private List<PersonEntity> searchWithAnyPosition(String firstname, String lastname, String patronymic,
                                                     String phoneNumber, String email,
                                                     LocalDate startDate, LocalDate endDate) {
        return personRepository.findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                firstname, lastname, patronymic, phoneNumber, email, startDate, endDate);
    }

    //patronymic is null
    private List<PersonEntity> searchWithNullPatronymic(String firstname, String lastname,
                                                        List<PositionEntity> positions, String phoneNumber, String email,
                                                        LocalDate startDate, LocalDate endDate) {
        return personRepository.findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicIsNullAndPositionInAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                firstname, lastname, positions, phoneNumber, email, startDate, endDate);
    }

    //full
    private List<PersonEntity> searchFull(String firstname, String lastname, String patronymic,
                                          List<PositionEntity> positions, String phoneNumber, String email,
                                          LocalDate startDate, LocalDate endDate) {
        return personRepository.findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPositionInAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                firstname, lastname, patronymic, positions, phoneNumber, email, startDate, endDate);
    }
    //endregion  Ut Utili

    @Override
    public List<PersonDTO> findAllByPosition(Long id) {
        isIdNotNull(id);
        return personRepository.findAllByPosition_Id(id).stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPosition(String position) {
        isNameNotBlank(position);
        return personRepository.findAllByPosition_NameEqualsIgnoreCase(position).stream().map(mapper::convert).toList();
    }
}
