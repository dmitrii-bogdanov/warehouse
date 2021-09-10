package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findAllByEmailIgnoreCase(String email);

    //TODO Check what it does
    List<PersonEntity> findAllByEmailContainingIgnoreCase(String partialEmail);

    List<PersonEntity> findAllByPhoneNumber(String phoneNumber);

    //TODO Check what it does
    List<PersonEntity> findAllByPhoneNumberContaining(String partialPhoneNumber);

    List<PersonEntity> findAllByFirstnameIgnoreCase(String firstname);

    List<PersonEntity> findAllByLastnameIgnoreCase(String lastname);

    List<PersonEntity> findAllByPatronymicIgnoreCase(String patronymic);

    List<PersonEntity> findAllByFirstnameIgnoreCaseAndLastnameIgnoreCaseAndPatronymicIgnoreCase(String firstname,
                                                                                                String lastname,
                                                                                                String patronymic);

    List<PersonEntity> findAllByFirstnameIgnoreCaseAndLastnameIgnoreCase(String firstname, String lastname);

    List<PersonEntity> findAllByFirstnameIgnoreCaseAndPatronymicIgnoreCase(String firstname, String patronymic);

    List<PersonEntity> findAllByLastnameIgnoreCaseAndPatronymicIgnoreCase(String lastname, String patronymic);

    List<PersonEntity> findAllByBirthEquals(LocalDate date);

    List<PersonEntity> findAllByBirthBefore(LocalDate date);

    List<PersonEntity> findAllByBirthAfter(LocalDate date);

    List<PersonEntity> findAllByBirthBetween(LocalDate start, LocalDate end);

    List<PersonEntity> findAllByPosition_NameEqualsIgnoreCase(String name);

    List<PersonEntity> findAllByPosition_Id(Long id);

    boolean existsByPosition_Id(Long id);

}
