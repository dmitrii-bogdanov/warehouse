package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordTypeRepository extends JpaRepository<RecordTypeEntity, Long> {

    Optional<RecordTypeEntity> findByName(String name);

    Optional<RecordTypeEntity> findByNameIgnoreCase(String name);

}
