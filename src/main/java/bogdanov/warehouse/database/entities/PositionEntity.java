package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
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

    public PositionEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
