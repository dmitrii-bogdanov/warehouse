package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO
@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {



}
