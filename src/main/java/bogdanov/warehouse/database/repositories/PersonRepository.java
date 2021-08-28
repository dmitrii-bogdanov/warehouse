package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    public List<PersonEntity> findAllByEmail(String email);

    //TODO Check what it does
    public List<PersonEntity> findAllByEmailContaining(String partialEmail);

    public List<PersonEntity> findAllByPhoneNumber(String phoneNumber);

    //TODO Check what it does
    public List<PersonEntity> findAllByPhoneNumberStartingWith(String startWith);

    public List<PersonEntity> findAllByFirstname(String firstname);

    public List<PersonEntity> findAllByLastname(String lastname);

    public List<PersonEntity> findAllByPatronymic(String patronymic);

    public List<PersonEntity> findAllByFirstnameAndLastnameAndPatronymic(String firstname,
                                                                         String lastname,
                                                                         String patronymic);

    public List<PersonEntity> findAllByBirthEquals(LocalDate date);

    public List<PersonEntity> findAllByBirthBefore(LocalDate date);

    public List<PersonEntity> findAllByBirthAfter(LocalDate date);

    public List<PersonEntity> findAllByBirthBetween(LocalDate start, LocalDate end);

    public List<PersonEntity> findAllByFirstnameAndPatronymicAndLastname(String firstname,
                                                                         String patronymic,
                                                                         String lastname);

    List<PersonEntity> findAllByPosition_NameEquals(String name);

    boolean existsByPosition_NameEquals(String name);

}
