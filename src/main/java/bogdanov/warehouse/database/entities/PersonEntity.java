package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "persons")
public class PersonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Column(nullable = false)
    private String patronymic = Strings.EMPTY;

    private String phoneNumber;

    private String email;

    private String company;

    @ManyToOne(fetch = FetchType.EAGER)
    private PositionEntity position;

}
