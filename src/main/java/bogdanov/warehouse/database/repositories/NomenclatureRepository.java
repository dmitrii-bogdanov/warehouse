package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NomenclatureRepository extends JpaRepository<NomenclatureEntity, Long> {

    public NomenclatureEntity getByCode(String code);

    public NomenclatureEntity getByName(String name);

    //TODO Check what it does
    public List<NomenclatureEntity> findAllByNameContaining(String partialName);

    public List<NomenclatureEntity> findAllByAmountGreaterThanEqual(long amount);

    public List<NomenclatureEntity> findAllByAmountLessThan(long amount);

    public List<NomenclatureEntity> findAllByAmountGreaterThan(long amount);

    public List<NomenclatureEntity> findAllByAmountEquals(long amount);

    public List<NomenclatureEntity> findAllByIsActive(boolean isActive);

}
