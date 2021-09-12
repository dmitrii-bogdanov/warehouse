package bogdanov.warehouse.database.entities;

import bogdanov.warehouse.database.enums.RecordType;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "record_types")
public class RecordTypeEntity {

    @Id
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    public RecordTypeEntity(RecordType type) {
        this.id = type.getId();
        this.name = type.name();
    }

}
