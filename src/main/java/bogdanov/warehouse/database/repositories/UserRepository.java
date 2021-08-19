package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
