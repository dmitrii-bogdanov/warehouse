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

    //full
    List<PersonEntity> findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPositionInAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
            String firstname, String lastname, String patronymic,
            List<PositionEntity> positions, String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate);

    //patronymic is null
    List<PersonEntity> findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicIsNullAndPositionInAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
            String firstname, String lastname,
            List<PositionEntity> positions, String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate);

    //patronymic is not null, any position
    List<PersonEntity> findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicContainingIgnoreCaseAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
            String firstname, String lastname, String patronymic,
            String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate);

    //patronymic is null, any position
    List<PersonEntity> findAllByFirstnameContainingIgnoreCaseAndLastnameContainingIgnoreCaseAndPatronymicIsNullAndPhoneNumberContainingAndEmailContainingIgnoreCaseAndBirthBetween(
            String firstname, String lastname,
            String phoneNumber, String email,
            LocalDate startDate, LocalDate endDate);

    List<PersonEntity> findAllByBirthEquals(LocalDate date);

    List<PersonEntity> findAllByBirthBefore(LocalDate date);

    List<PersonEntity> findAllByBirthAfter(LocalDate date);

    List<PersonEntity> findAllByBirthBetween(LocalDate start, LocalDate end);

    List<PersonEntity> findAllByPosition_NameEqualsIgnoreCase(String name);

    List<PersonEntity> findAllByPosition_Id(Long id);

    boolean existsByPosition_Id(Long id);

}
