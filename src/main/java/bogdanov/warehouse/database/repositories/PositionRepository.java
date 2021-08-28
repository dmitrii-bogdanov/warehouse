package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Long> {

    public Optional<PositionEntity> findByName(String name);

    public List<PositionEntity> findAllByNameContaining(String partialName);

    public boolean existsByName(String name);

    public void deleteByName(String name);
}
