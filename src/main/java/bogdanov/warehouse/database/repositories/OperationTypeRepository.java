package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.OperationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationTypeEntity, Long> {

    OperationTypeEntity findByName(String name);

}
