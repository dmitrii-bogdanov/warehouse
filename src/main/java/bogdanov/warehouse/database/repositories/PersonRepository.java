package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<PersonEntity, Long> {

    List<PersonEntity> findAllByEmailIgnoreCase(String email);

    List<PersonEntity> findAllByEmailContainingIgnoreCase(String partialEmail);

    List<PersonEntity> findAllByPhoneNumber(String phoneNumber);

    List<PersonEntity> findAllByPhoneNumberContaining(String partialPhoneNumber);

    List<PersonEntity> findAllByFirstnameIgnoreCase(String firstname);

    List<PersonEntity> findAllByLastnameIgnoreCase(String lastname);

    default List<PersonEntity> findAllByFullName(String firstname, String lastname, String patronymic) {
        return findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCase(
                firstname, lastname, patronymic);
    }

    default List<PersonEntity> findAllByFullNameWithNullPatronymic(String firstname, String lastname) {
        return findAllByFirstnameIgnoreCaseAndLastnameIgnoreCaseAndPatronymicIsNull(firstname, lastname);
    }

    List<PersonEntity>
    findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCase(
            String firstname, String lastname, String patronymic);

    List<PersonEntity> findAllByFirstnameIgnoreCaseAndLastnameIgnoreCaseAndPatronymicIsNull(String firstname,
                                                                                            String lastname);

    List<PersonEntity> findAllByBirthEquals(LocalDate date);

    List<PersonEntity> findAllByBirthBefore(LocalDate date);

    List<PersonEntity> findAllByBirthAfter(LocalDate date);

    List<PersonEntity> findAllByBirthBetween(LocalDate start, LocalDate end);

    List<PersonEntity> findAllByPosition_NameEqualsIgnoreCase(String name);

    List<PersonEntity> findAllByPosition_Id(Long id);

    boolean existsByPosition_Id(Long id);

}
