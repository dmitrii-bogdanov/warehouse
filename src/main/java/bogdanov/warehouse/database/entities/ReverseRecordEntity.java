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
public class ReverseRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private RecordEntity revertedRecord;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    private RecordEntity generatedRecord;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity user;

    public ReverseRecordEntity(RecordEntity revertedRecord,
                               RecordEntity generatedRecord,
                               LocalDateTime time,
                               UserEntity user) {
        this.revertedRecord = revertedRecord;
        this.generatedRecord = generatedRecord;
        this.time = time;
        this.user = user;
    }
}
