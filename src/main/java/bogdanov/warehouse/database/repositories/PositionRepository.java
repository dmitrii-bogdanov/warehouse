package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    Optional<PositionEntity> findByNameIgnoreCase(String name);

    List<PositionEntity> findAllByNameContainingIgnoreCase(String partialName);

}
