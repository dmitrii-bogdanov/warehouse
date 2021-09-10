package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.dto.PersonDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

//TODO
@Service
public interface PersonService {

    PersonDTO add(PersonDTO person);

    List<PersonDTO> add(List<PersonDTO> persons);

    PersonDTO update(PersonDTO person);

    List<PersonDTO> update(List<PersonDTO> person);

    PersonDTO delete(Long id);

    List<PersonDTO> getAll();

    PersonDTO getById(Long id);

    PersonEntity getEntityById(Long id);

    List<PersonDTO> findAllByFirstname(String firstname);

    List<PersonDTO> findAllByLastname(String lastname);

    List<PersonDTO> findAllByBirthDate(LocalDate date);

    List<PersonDTO> findAllOlderThan(Integer age);

    List<PersonDTO> findAllYoungerThan(Integer age);

    List<PersonDTO> findAllWithBirthDateBetween(LocalDate start, LocalDate end);

    List<PersonDTO> findAllByPhoneNumber(String phoneNumber);

    List<PersonDTO> findAllByPhoneNumberContaining(String partialPhoneNumber);

    List<PersonDTO> findAllByEmail(String email);

    List<PersonDTO> findAllByEmailContaining(String partialEmail);

    List<PersonDTO> findAllByFullName(String firstname, String patronymic, String lastname);

    List<PersonDTO> findAllByPosition(Long id);

    List<PersonDTO> findAllByPosition(String position);

}
