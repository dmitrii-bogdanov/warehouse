package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.PositionEntity;
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

    default List<PersonEntity> search(
            String firstname, String lastname, String patronymic,
            Long positionId, String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate) {
        return findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPosition_IdAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                firstname, lastname, patronymic, positionId, phoneNumber, email, startDate, endDate);
    }

    default List<PersonEntity> searchWithNullPatronymic(
            String firstname, String lastname,
            Long positionId, String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate) {
        return findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicIsNullAndPosition_IdAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
                firstname, lastname, positionId, phoneNumber, email, startDate, endDate);
    }

    List<PersonEntity>
    findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCase(
            String firstname, String lastname, String patronymic);

    List<PersonEntity> findAllByFirstnameIgnoreCaseAndLastnameIgnoreCaseAndPatronymicIsNull(String firstname,
                                                                                            String lastname);

    List<PersonEntity> findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPosition_IdAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
            String firstname, String lastname, String patronymic,
            Long positionId, String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate);

    List<PersonEntity> findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicIsNullAndPosition_IdAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
            String firstname, String lastname,
            Long positionId, String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate);

    List<PersonEntity> findAllByBirthEquals(LocalDate date);

    List<PersonEntity> findAllByBirthBefore(LocalDate date);

    List<PersonEntity> findAllByBirthAfter(LocalDate date);

    List<PersonEntity> findAllByBirthBetween(LocalDate start, LocalDate end);

    List<PersonEntity> findAllByPosition_NameEqualsIgnoreCase(String name);

    List<PersonEntity> findAllByPosition_Id(Long id);

    boolean existsByPosition_Id(Long id);

}
