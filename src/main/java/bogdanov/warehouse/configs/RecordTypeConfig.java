package bogdanov.warehouse.configs;

import bogdanov.warehouse.database.entities.RecordTypeEntity;
import bogdanov.warehouse.database.enums.RecordType;
import bogdanov.warehouse.database.repositories.RecordTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class RecordTypeConfig {

    @Autowired
    private void initializeRecordTypes(RecordTypeRepository recordTypeRepository) {
        recordTypeRepository.saveAll(
                Arrays.stream(RecordType.values()).map(RecordTypeEntity::new).toList());
    }


}
