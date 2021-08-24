package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NomenclatureRepository extends JpaRepository<NomenclatureEntity, Long> {

    public Optional<NomenclatureEntity> findByCode(String code);

    public Optional<NomenclatureEntity> findByName(String name);

    public List<NomenclatureEntity> findAllByCode(String code);
    //TODO Check what it does
    public List<NomenclatureEntity> findAllByNameContaining(String partialName);

    public List<NomenclatureEntity> findAllByAmountGreaterThanEqual(long amount);

    public List<NomenclatureEntity> findAllByAmountLessThan(long amount);

    public List<NomenclatureEntity> findAllByAmountGreaterThan(long amount);

    public List<NomenclatureEntity> findAllByAmountEquals(long amount);

    public List<NomenclatureEntity> findAllByCodeContaining(String partialCode);
}
