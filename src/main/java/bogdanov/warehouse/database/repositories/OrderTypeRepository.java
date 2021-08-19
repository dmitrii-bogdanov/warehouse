package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.OrderTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderTypeRepository extends JpaRepository<OrderTypeEntity, Long> {

    public OrderTypeEntity findByName(String name);

}
