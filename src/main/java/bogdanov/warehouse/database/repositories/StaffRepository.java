package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.StaffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO
@Repository
public interface StaffRepository extends JpaRepository<StaffEntity, Long> {
}
