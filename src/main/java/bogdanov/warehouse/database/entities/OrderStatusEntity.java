package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "order_statuses")
public class OrderStatusEntity {

    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isDeprecated = false;

    private String definition;

    protected OrderStatusEntity() {
        super();
    }

    public OrderStatusEntity(long id, String name){
        this.id = id;
        this.name = name;
    }

    public OrderStatusEntity(long id, String name, String definition){
        this.id = id;
        this.name = name;
        this.definition = definition;
    }

}
