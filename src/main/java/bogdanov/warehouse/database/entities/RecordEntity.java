package bogdanov.warehouse.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private NomenclatureEntity nomenclature;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private RecordTypeEntity type;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

}
