package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.OrderStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatusEntity, Long> {

    public OrderStatusEntity findByName(String name);

}
