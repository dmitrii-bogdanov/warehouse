package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.RoleEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//TODO
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    //TODO Check what it does
    List<UserEntity> findAllByUsernameStartingWith(String startsWith);
//
//    List<UserEntity> findAllByEnabled(boolean isEnabled);

    List<UserEntity> findAllByRoles_NameEqualsIgnoreCase(String name);

    List<UserEntity> findAllByRoles_IdEquals(Long id);

    Optional<UserEntity> findByPersonEquals(PersonEntity person);

    boolean existsByPersonEquals(PersonEntity person);

    boolean existsByUsername(String username);

    List<UserEntity> findAllByPerson_FirstnameIgnoreCaseAndPerson_PatronymicIgnoreCaseAndPerson_LastnameIgnoreCase(
            String firstname, String patronymic, String lastname);

    List<UserEntity> findAllByPerson_Position_NameIgnoreCase(String position);

    Optional<UserEntity> findByPerson_Id(Long id);

    boolean existsByPerson_Id(Long id);
}
