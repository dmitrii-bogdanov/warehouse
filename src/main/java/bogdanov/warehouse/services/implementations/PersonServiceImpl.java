package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.services.interfaces.PersonService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

//TODO
@Service
public class PersonServiceImpl implements PersonService {


    @Override
    public PersonDTO add(PersonDTO person) {
        return null;
    }

    @Override
    public List<PersonDTO> add(List<PersonDTO> persons) {
        return null;
    }

    @Override
    public PersonDTO update(PersonDTO person) {
        return null;
    }

    @Override
    public List<PersonDTO> update(List<PersonDTO> person) {
        return null;
    }

    @Override
    public List<PersonDTO> getAll() {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByFirstname(String firstname) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByLastname(String lastname) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByPatronymic(String patronymic) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByBirthDate(LocalDate date) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllOlderThan(int age) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllYoungerThan(int age) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByPhoneNumberStartingWith(String startWith) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByEmail(String email) {
        return null;
    }

    @Override
    public List<PersonDTO> findAllByEmailContaining(String partialEmail) {
        return null;
    }
}
