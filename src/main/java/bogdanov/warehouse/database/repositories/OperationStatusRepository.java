package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.OperationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationStatusRepository extends JpaRepository<OperationStatusEntity, Long> {

    public OperationStatusEntity findByName(String name);

}
