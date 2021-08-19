package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//TODO
@Repository
public interface NomenclatureRepository extends JpaRepository<NomenclatureEntity, Long> {
}
