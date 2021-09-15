package bogdanov.warehouse.database.entities;

import lombok.*;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.util.Locale;

//TODO
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "nomenclature")
public class NomenclatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true, unique = true)
    private String code;

    @Column(nullable = false)
    private long amount = 0L;

    public NomenclatureEntity(Long id, String name, String code, Long amount) {
        this.id = id;
        setName(name);
        setCode(code);
        setAmount(amount);
    }

    public void add(long amount) {
        this.amount += amount;
    }

    public boolean take(long amount) {
        if (amount > 0L) {
            if (this.amount >= amount) {
                setAmount(this.amount - amount);
                return true;
            }
        }
        return false;
    }

    public void setAmount(Long amount) {
        if (amount != null) {
            this.amount = amount;
        } else {
            this.amount = 0L;
        }
    }
}
