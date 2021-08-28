package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomenclatureRepository extends JpaRepository<NomenclatureEntity, Long> {

    Optional<NomenclatureEntity> findByCode(String code);

    Optional<NomenclatureEntity> findByName(String name);

    List<NomenclatureEntity> findAllByCode(String code);

    List<NomenclatureEntity> findAllByNameContaining(String partialName);

    List<NomenclatureEntity> findAllByAmountGreaterThanEqual(long amount);

    List<NomenclatureEntity> findAllByAmountLessThan(long amount);

    List<NomenclatureEntity> findAllByAmountGreaterThan(long amount);

    List<NomenclatureEntity> findAllByAmountEquals(long amount);

    List<NomenclatureEntity> findAllByCodeContaining(String partialCode);

    List<NomenclatureEntity> findAllByNameContainingAndCodeContaining(String partialName, String partialCode);
}
