package bogdanov.warehouse.database.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "persons")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column
    private String patronymic = Strings.EMPTY;

    @Column(nullable = false)
    private LocalDate birth;

    private String phoneNumber;

    private String email;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private PositionEntity position;

}
