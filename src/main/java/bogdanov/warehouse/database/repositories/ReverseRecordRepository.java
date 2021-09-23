package bogdanov.warehouse.database.repositories;

import bogdanov.warehouse.database.entities.ReverseRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReverseRecordRepository extends JpaRepository<ReverseRecordEntity, Long> {

    Optional<ReverseRecordEntity> findByRevertedRecord_Id(Long id);

}
