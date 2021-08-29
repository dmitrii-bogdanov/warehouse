package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.repositories.PersonRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.exceptions.AlreadyRegisteredPersonException;
import bogdanov.warehouse.exceptions.NotAllRequiredFieldsPresentException;
import bogdanov.warehouse.exceptions.NullIdException;
import bogdanov.warehouse.exceptions.ResourceNotFoundException;
import bogdanov.warehouse.services.interfaces.PersonService;
import bogdanov.warehouse.services.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;

    @Override
    public PersonDTO add(PersonDTO person) {
        if (person.allRequiredFieldsPresent()) {
            return mapper.convert(personRepository.save(mapper.convert(person)));
        } else {
            throw new NotAllRequiredFieldsPresentException(
                    "Person firstname, lastname and date of birth should be present");
        }
    }

    @Override
    public List<PersonDTO> add(List<PersonDTO> persons) {
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(PersonDTO::allRequiredFieldsPresent)
                .distinct()
                .map(mapper::convert)
                .toList();
        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public PersonDTO update(PersonDTO person) {
        if (person.allRequiredFieldsPresent()) {
            Optional<PersonEntity> optionalEntity = personRepository.findById(person.getId());
            if (optionalEntity.isPresent()) {
                return mapper.convert(personRepository.save(mapper.convert(person)));
            } else {
                throw new ResourceNotFoundException("Person with id : " + person.getId() + " not found");
            }
        } else {
            throw new NotAllRequiredFieldsPresentException(
                    "Person firstname, lastname and date of birth should be present");
        }
    }

    @Override
    public List<PersonDTO> update(List<PersonDTO> persons) {
        List<PersonEntity> entities;
        entities = persons
                .stream()
                .filter(PersonDTO::allRequiredFieldsPresent)
                .distinct()
                .map(dto -> personRepository.findById(dto.getId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        entities = personRepository.saveAll(entities);
        return entities.stream().map(mapper::convert).toList();
    }

    @Override
    public PersonDTO delete(Long id) {
        if (id == null) {
            throw new NullIdException("Id value is missing");
        }
        Optional<PersonEntity> entity = personRepository.findById(id);
        if (entity.isPresent()) {
            if (userRepository.existsByPerson_Id(id)) {
                throw new AlreadyRegisteredPersonException("Person with id : " + id + " already registered as user");
            } else {
                personRepository.delete(entity.get());
                return mapper.convert(entity.get());
            }
        } else {
            throw new ResourceNotFoundException("Person with id : " + id + " not found");
        }
    }

    @Override
    public PersonDTO getById(Long id) {
        return mapper.convert(getEntityById(id));
    }

    @Override
    public PersonEntity getEntityById(Long id) {
        if (id == null) {
            throw new NullIdException("Person id is null");
        }
        Optional<PersonEntity> optionalEntity = personRepository.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get();
        } else {
            throw new ResourceNotFoundException(
                    "Person with id : " + id + " not found");
        }
    }

    @Override
    public List<PersonDTO> getAll() {
        return personRepository.findAll().stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByFirstname(String firstname) {
        return personRepository
                .findAllByFirstname(firstname.toUpperCase(Locale.ROOT))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByLastname(String lastname) {
        return personRepository
                .findAllByLastname(lastname.toUpperCase(Locale.ROOT))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPatronymic(String patronymic) {
        return personRepository
                .findAllByPatronymic(patronymic.toUpperCase(Locale.ROOT))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByBirthDate(LocalDate date) {
        return personRepository.findAllByBirthEquals(date)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllOlderThan(int age) {
        return personRepository.findAllByBirthBefore(LocalDate.now().minusYears(age))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllYoungerThan(int age) {
        return personRepository.findAllByBirthAfter(LocalDate.now().minusYears(age))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllWithBirthDateBetween(LocalDate start, LocalDate end) {
        return personRepository.findAllByBirthBetween(start, end)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPhoneNumber(String phoneNumber) {
        return personRepository.findAllByPhoneNumber(phoneNumber)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByPhoneNumberStartingWith(String startWith) {
        return personRepository.findAllByPhoneNumberStartingWith(startWith)
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByEmail(String email) {
        return personRepository.findAllByEmail(email.toUpperCase(Locale.ROOT))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByEmailContaining(String partialEmail) {
        return personRepository.findAllByEmailContaining(partialEmail.toUpperCase(Locale.ROOT))
                .stream().map(mapper::convert).toList();
    }

    @Override
    public List<PersonDTO> findAllByFullName(String firstname, String patronymic, String lastname) {
        return personRepository
                .findAllByFirstnameAndPatronymicAndLastname(firstname, patronymic, lastname)
                .stream().map(mapper::convert).toList();
    }
}
