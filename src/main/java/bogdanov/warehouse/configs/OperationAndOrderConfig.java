package bogdanov.warehouse.configs;

import bogdanov.warehouse.database.entities.OperationStatusEntity;
import bogdanov.warehouse.database.entities.OperationTypeEntity;
import bogdanov.warehouse.database.entities.OrderStatusEntity;
import bogdanov.warehouse.database.entities.OrderTypeEntity;
import bogdanov.warehouse.database.enums.OperationStatus;
import bogdanov.warehouse.database.enums.OperationType;
import bogdanov.warehouse.database.enums.OrderStatus;
import bogdanov.warehouse.database.enums.OrderType;
import bogdanov.warehouse.database.repositories.OperationStatusRepository;
import bogdanov.warehouse.database.repositories.OperationTypeRepository;
import bogdanov.warehouse.database.repositories.OrderStatusRepository;
import bogdanov.warehouse.database.repositories.OrderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperationAndOrderConfig {

    @Autowired
    private void initializeOperationTypes(OperationTypeRepository repository) {
        for (OperationType type : OperationType.values()) {
            repository.save(new OperationTypeEntity(type.getId(), type.name()));
        }
    }

    @Autowired
    private void initializeOperationStatuses(OperationStatusRepository repository) {
        for (OperationStatus status : OperationStatus.values()) {
            repository.save(new OperationStatusEntity(status.getId(), status.name()));
        }
    }

    @Autowired
    private void initializeOrderTypes(OrderTypeRepository repository) {
        for (OrderType type : OrderType.values()) {
            repository.save(new OrderTypeEntity(type.getId(), type.name()));
        }
    }

    @Autowired
    private void initializeOrderStatuses(OrderStatusRepository repository) {
        for (OrderStatus status : OrderStatus.values()) {
            repository.save(new OrderStatusEntity(status.getId(), status.name()));
        }
    }

}
