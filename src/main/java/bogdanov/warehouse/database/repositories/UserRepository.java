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

    public UserEntity findByUsername(String username);

    //TODO Check what it does
    public List<UserEntity> findAllByUsernameStartingWith(String startsWith);

    public List<UserEntity> findAllByEnabledIs(boolean isEnabled);

    public List<UserEntity> findAllByRoles_NameEquals(String name);

    public List<UserEntity> findAllByRoles_IdEquals(Long id);

    public Optional<UserEntity> findByPersonEquals(PersonEntity person);

    public boolean existsByPersonEquals(PersonEntity person);

    public boolean existsByUsername(String username);
}
