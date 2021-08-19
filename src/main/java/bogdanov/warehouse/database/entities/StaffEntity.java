package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "staff")
public class StaffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private PositionEntity position;

    @OneToOne(fetch = FetchType.EAGER)
    private PersonEntity person;

    @Transient
    @OneToOne(mappedBy = "staff")
    private UserEntity user;

}
