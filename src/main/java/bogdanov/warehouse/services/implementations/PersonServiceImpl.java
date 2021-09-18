package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.search.SearchPersonDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.interfaces.PositionService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PositionService positionService;

    private static final String PERSON = "Person";
    private static final String ID = "id";
    private static final String PATRONYMIC = "patronymic";
    private static final String SEARCH_POSITION_DELIMITER = ",";
    private static final String FROM_DATE = "fromDate";
    private static final String TO_DATE = "toDate";
    private static final String POSITION = "Position";
    private static final String EXISTING_REFERENCE_SUBSTRING = "REFERENCES PUBLIC.PERSON";
    private static final String USER_FOREIGN_KEY_SUBSTRING = "PUBLIC.USERS FOREIGN KEY(PERSON";


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

    private void checkId(Long id) {
        if (id == null) {
            throw new ArgumentException(ExceptionType.NULL_ID);
        }
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

    private void checkListNotEmpty(Collection<PersonDTO> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new ArgumentException(ExceptionType.NO_OBJECT_WAS_PASSED);
        }
    }

    private List<PositionEntity> formatDtoAndReturnPositions(SearchPersonDTO dto) {

        boolean isFirstnameBlank = Strings.isBlank(dto.getFirstname());
        boolean isLastnameBlank = Strings.isBlank(dto.getLastname());
        boolean isPositionAbsent = dto.getPositions() == null || dto.getPositions().isEmpty();
        boolean isPhoneNumberBlank = Strings.isBlank(dto.getPhoneNumber());
        boolean isPatronymicBlank = Strings.isBlank(dto.getPatronymic());
        boolean isEmailBlank = Strings.isBlank(dto.getEmail());
        boolean isFromDateAbsent = dto.getFromDate() == null;
        boolean isToDateAbsent = dto.getToDate() == null;
        List<PositionEntity> positions = new LinkedList<>();

        if (isFirstnameBlank && isLastnameBlank && isPatronymicBlank
                && isPositionAbsent && isPhoneNumberBlank && isEmailBlank
                && isFromDateAbsent && isToDateAbsent) {
            throw new ArgumentException(ExceptionType.NO_PARAMETER_IS_PRESENT);
        }

        if (isFirstnameBlank) {
            dto.setFirstname(Strings.EMPTY);
        }
        if (isLastnameBlank) {
            dto.setLastname(Strings.EMPTY);
        }
        if (isPatronymicBlank) {
            dto.setPatronymic(Strings.EMPTY);
        }
        if (isPhoneNumberBlank) {
            dto.setPhoneNumber(Strings.EMPTY);
        }
        if (isEmailBlank) {
            dto.setEmail(Strings.EMPTY);
        }
        if (isFromDateAbsent) {
            dto.setFromDate(LocalDate.of(0, 1, 1));
        }
        if (isToDateAbsent) {
            dto.setToDate(LocalDate.of(2200, 12, 31));
        }
        if (dto.getFromDate().compareTo(dto.getToDate()) > 0) {
            throw new ArgumentException(ExceptionType.INCORRECT_RANGE.setFrom(FROM_DATE).setTo(TO_DATE));
        }
        if (!isPositionAbsent) {
            positions = dto.getPositions().stream().map(positionService::getEntityById).toList();
        }

        return positions;
    }

    private List<PersonEntity> search(SearchPersonDTO dto, List<PositionEntity> positions) {
        return positions.isEmpty()
                ? personRepository.findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                dto.getFirstname(), dto.getLastname(), dto.getPatronymic(), dto.getPhoneNumber(), dto.getEmail(), dto.getFromDate(), dto.getToDate())
                : personRepository.findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPositionInAndPhoneNumberContainingIgnoreCaseAndEmailContainingIgnoreCaseAndBirthBetween(
                dto.getFirstname(), dto.getLastname(), dto.getPatronymic(), positions, dto.getPhoneNumber(), dto.getEmail(), dto.getFromDate(), dto.getToDate());
    }
    //endregion

    @Override
    public List<PersonDTO> add(List<PersonDTO> persons) {
        checkListNotEmpty(persons);
        for (PersonDTO p : persons) {
            p.setId(null);
        }
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(this::areAllRequiredFieldsPresent)
                .map(mapper::convert)
                .toList();
        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> update(List<PersonDTO> persons) {
        checkListNotEmpty(persons);
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(this::areAllRequiredFieldsPresent)
                .map(mapper::convert)
                .toList();

        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public PersonDTO delete(Long id) {
        PersonEntity entity = getEntityById(id);
        try {
            personRepository.delete(entity);
        } catch (DataIntegrityViolationException e) {
            throw wrapException(e, entity);
        }
        return mapper.convert(entity);
    }

    @Override
    public PersonDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public PersonEntity getEntityById(Long id) {
        checkId(id);
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
    public List<PersonDTO> search(SearchPersonDTO dto) {
        return search(dto, formatDtoAndReturnPositions(dto)).stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPosition(Long id) {
        PositionEntity position = positionService.getEntityById(id);
        return personRepository.findAllByPositionEquals(positionService.getEntityById(id))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPosition(String position) {
        return personRepository.findAllByPositionEquals(positionService.getEntityByName(position))
                .stream().map(mapper::convert).toList();
    }
}
