package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    public RoleEntity getByName(String name);

}
