package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    List<PersonEntity> findAllByEmail(String email);

    //TODO Check what it does
    List<PersonEntity> findAllByEmailContaining(String partialEmail);

    List<PersonEntity> findAllByPhoneNumber(String phoneNumber);

    //TODO Check what it does
    List<PersonEntity> findAllByPhoneNumberStartingWith(String startWith);

    List<PersonEntity> findAllByFirstname(String firstname);

    List<PersonEntity> findAllByLastname(String lastname);

    List<PersonEntity> findAllByPatronymic(String patronymic);

    List<PersonEntity> findAllByFirstnameAndLastnameAndPatronymic(String firstname,
                                                                  String lastname,
                                                                  String patronymic);

    List<PersonEntity> findAllByBirthEquals(LocalDate date);

    List<PersonEntity> findAllByBirthBefore(LocalDate date);

    List<PersonEntity> findAllByBirthAfter(LocalDate date);

    List<PersonEntity> findAllByBirthBetween(LocalDate start, LocalDate end);

    List<PersonEntity> findAllByFirstnameAndPatronymicAndLastname(String firstname,
                                                                  String patronymic,
                                                                  String lastname);

    List<PersonEntity> findAllByPosition_NameEquals(String name);

    boolean existsByPosition_NameEquals(String name);

}
