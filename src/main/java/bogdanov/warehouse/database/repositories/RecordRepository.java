package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

    List<RecordEntity> findAllByNomenclature_Id(long id);

    List<RecordEntity> findAllByNomenclature_NameEquals(String nomenclatureName);

    List<RecordEntity> findAllByNomenclature_CodeEquals(String nomenclatureCode);

    List<RecordEntity> findAllByUser_Id(long id);

    List<RecordEntity> findAllByType_NameEquals(String type);

}
