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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isDeprecated = false;

    protected PositionEntity() {
        super();
    }

    public PositionEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Transient
    @OneToMany(mappedBy = "position", fetch = FetchType.LAZY)
    private Collection<PersonEntity> persons;

}
