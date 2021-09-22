package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import bogdanov.warehouse.database.entities.RecordEntity;
import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {

    List<RecordEntity> findAllByNomenclature_Id(long id);

    List<RecordEntity> findAllByNomenclature_NameEquals(String nomenclatureName);

    List<RecordEntity> findAllByNomenclature_CodeEquals(String nomenclatureCode);

    List<RecordEntity> findAllByUser_Id(long id);

    List<RecordEntity> findAllByUser_UsernameEquals(String name);

    List<RecordEntity> findAllByType_NameEquals(String type);

    boolean existsByNomenclature_Id(Long id);

    boolean existsByUser_Id(Long id);

    List<RecordEntity> findAllByTimeBetween(LocalDateTime start, LocalDateTime end);

    List<RecordEntity> findAllByTypeInAndTimeBetween(
            List<RecordTypeEntity> types, LocalDateTime start, LocalDateTime end);

    List<RecordEntity> findAllByTypeInAndNomenclatureInAndUserInAndTimeBetween(
            List<RecordTypeEntity> types, List<NomenclatureEntity> nomenclature, List<UserEntity> users,
            LocalDateTime start, LocalDateTime end);

    List<RecordEntity> findAllByTypeInAndNomenclatureInAndTimeBetween(
            List<RecordTypeEntity> types, List<NomenclatureEntity> nomenclature,
            LocalDateTime start, LocalDateTime end);

    List<RecordEntity> findAllByTypeInAndUserInAndTimeBetween(
            List<RecordTypeEntity> types, List<UserEntity> users,
            LocalDateTime start, LocalDateTime end);
}
