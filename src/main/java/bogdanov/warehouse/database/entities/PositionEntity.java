package bogdanov.warehouse.database.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@Table(name = "positions")
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    protected PositionEntity() {
        super();
    }

    public PositionEntity(String name) {
        this.name = name;
    }

}
