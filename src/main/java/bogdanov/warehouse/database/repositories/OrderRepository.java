package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
