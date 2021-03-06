package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomenclatureRepository extends JpaRepository<NomenclatureEntity, Long> {

    Optional<NomenclatureEntity> findByCodeIgnoreCase(String code);

    Optional<NomenclatureEntity> findByNameIgnoreCase(String name);

    List<NomenclatureEntity> findAllByCodeIsNull();

    List<NomenclatureEntity> findAllByNameContainingIgnoreCase(String partialName);

    List<NomenclatureEntity> findAllByCodeContainingIgnoreCase(String partialCode);

    List<NomenclatureEntity> findAllByNameContainingIgnoreCaseAndCodeIsNullAndAmountBetween(String partialName,
                                                                                            Long minAmount,
                                                                                            Long maxAmount);

    List<NomenclatureEntity> findAllByNameContainingIgnoreCaseAndCodeContainingIgnoreCaseAndAmountBetween(
            String partialName, String partialCode, Long minAmount, Long maxAmount);

    List<NomenclatureEntity> findAllByNameContainingIgnoreCaseAndAmountBetween(
            String partialName, Long minAmount, Long maxAmount);

    List<NomenclatureEntity> findAllByAmountGreaterThanEqual(long amount);

    List<NomenclatureEntity> findAllByAmountLessThan(long amount);

    List<NomenclatureEntity> findAllByAmountGreaterThan(long amount);

    List<NomenclatureEntity> findAllByAmountEquals(long amount);

}
