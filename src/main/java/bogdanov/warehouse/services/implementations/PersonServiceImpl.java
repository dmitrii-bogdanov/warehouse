package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;

    private boolean areAllRequiredFieldsPresent(PersonDTO dto) {
        if (Strings.isNotBlank(dto.getFirstname())
                && Strings.isNotBlank(dto.getLastname())
                && dto.getBirth() != null
                && Strings.isNotBlank(dto.getPosition())) {
            return true;
        } else {
            throw new IllegalArgumentException(ExceptionMessage.NOT_ALL_PERSON_REQUIRED_FIELDS.getMessage());
        }
    }

    @Override
    public PersonDTO add(PersonDTO person) {
        /*TODO check alternatives
         *TODO implement additional convert() method
         *TODO in mapper to add new records
         */
        areAllRequiredFieldsPresent(person);
        PersonEntity entity = mapper.convert(person);
        entity.setId(null);
        return mapper.convert(personRepository.save(entity));
//            return mapper.convert(personRepository.save(mapper.convert(person)));
    }

    @Override
    public List<PersonDTO> add(List<PersonDTO> persons) {
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(this::areAllRequiredFieldsPresent)
                .map(mapper::convert)
                //TODO check alternative
                .peek(e -> e.setId(null))
                //
                .toList();
        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public PersonDTO update(PersonDTO person) {
        areAllRequiredFieldsPresent(person);
        getEntityById(person.getId());
        return mapper.convert(personRepository.save(mapper.convert(person)));

    }

    @Override
    public List<PersonDTO> update(List<PersonDTO> persons) {
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(this::areAllRequiredFieldsPresent)
                .filter(dto -> getEntityById(dto.getId()) != null)
                .map(mapper::convert)
                .toList();

        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public PersonDTO delete(Long id) {
        PersonEntity entity = getEntityById(id);
        if (userRepository.existsByPerson_Id(id)) {
            throw new ProhibitedRemovingException(ExceptionMessage.ALREADY_REGISTERED_PERSON.setId(id).getModifiedMessage());
        } else {
            personRepository.delete(entity);
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
            throw new ResourceNotFoundException("Person", "id", id);
        }
    }

    @Override
    public List<PersonDTO> getAll() {
        return personRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByFirstname(String firstname) {
        if (Strings.isBlank(firstname)) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByFirstnameIgnoreCase(firstname).stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByLastname(String lastname) {
        if (Strings.isBlank(lastname)) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByLastnameIgnoreCase(lastname).stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByBirthDate(LocalDate date) {
        if (date == null) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByBirthEquals(date)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllOlderThan(Integer age) {
        if (age == null) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByBirthBefore(LocalDate.now().minusYears(age))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllYoungerThan(Integer age) {
        if (age == null) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByBirthAfter(LocalDate.now().minusYears(age))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllWithBirthDateBetween(LocalDate start, LocalDate end) {
        boolean isStartAbsent = start == null;
        boolean isEndAbsent = end == null;
        if (isStartAbsent && isEndAbsent) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        List<PersonEntity> entities;
        if (isStartAbsent) {
            entities = personRepository.findAllByBirthBefore(end);
        } else if (isEndAbsent) {
            entities = personRepository.findAllByBirthAfter(start);
        } else if (start.equals(end)) {
            return findAllByBirthDate(start);
        } else {
            entities = personRepository.findAllByBirthBetween(start, end);
        }
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPhoneNumber(String phoneNumber) {
        if (Strings.isBlank(phoneNumber)) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByPhoneNumber(phoneNumber)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPhoneNumberContaining(String partialPhoneNumber) {
        if (Strings.isBlank(partialPhoneNumber)) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByPhoneNumberContaining(partialPhoneNumber)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByEmail(String email) {
        if (Strings.isBlank(email)) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByEmailIgnoreCase(email).stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByEmailContaining(String partialEmail) {
        if (Strings.isBlank(partialEmail)) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        return personRepository.findAllByEmailContainingIgnoreCase(partialEmail)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByFullName(String firstname, String patronymic, String lastname) {
        boolean isFirstnameBlank = Strings.isBlank(firstname);
        boolean isLastnameBlank = Strings.isBlank(lastname);
        boolean isPatronymicBlank = Strings.isBlank(patronymic);

        List<PersonEntity> entities;

        if (!isPatronymicBlank) {
            patronymic = patronymic.toUpperCase(Locale.ROOT);
            if ("NULL".equals(patronymic)) {
                patronymic = Strings.EMPTY;
            }
        }

        if (isFirstnameBlank && isLastnameBlank && isPatronymicBlank) {
            throw new ArgumentException(ExceptionMessage.NO_PARAMETER_IS_PRESENT);
        }
        if (isFirstnameBlank && isPatronymicBlank) {
            entities = personRepository.findAllByLastnameIgnoreCase(lastname);
        } else if (isLastnameBlank && isPatronymicBlank) {
            entities = personRepository.findAllByFirstnameIgnoreCase(firstname);
        } else if (isFirstnameBlank && isLastnameBlank) {
            entities = personRepository.findAllByPatronymicIgnoreCase(patronymic);
        } else if (isPatronymicBlank) {
            entities = personRepository.findAllByFirstnameIgnoreCaseAndLastnameIgnoreCase(firstname, lastname);
        } else if (isFirstnameBlank) {
            entities = personRepository.findAllByLastnameIgnoreCaseAndPatronymicIgnoreCase(lastname, patronymic);
        } else if (isLastnameBlank) {
            entities = personRepository.findAllByFirstnameIgnoreCaseAndPatronymicIgnoreCase(firstname, patronymic);
        } else {
            entities = personRepository
                    .findAllByFirstnameIgnoreCaseAndLastnameIgnoreCaseAndPatronymicIgnoreCase(
                            firstname,
                            lastname,
                            patronymic);
        }

        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPosition(Long id) {
        return personRepository.findAllByPosition_Id(id).stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPosition(String position) {
        if (Strings.isBlank(position)) {
            throw new ArgumentException(ExceptionMessage.BLANK_ENTITY_NAME.setEntity(PositionEntity.class));
        }
        return personRepository.findAllByPosition_NameEqualsIgnoreCase(position).stream().map(mapper::convert).toList();
    }
}
