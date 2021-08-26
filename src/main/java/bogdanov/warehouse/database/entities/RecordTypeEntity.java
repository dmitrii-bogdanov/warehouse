package bogdanov.warehouse.database.entities;

import bogdanov.warehouse.database.enums.RecordType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
