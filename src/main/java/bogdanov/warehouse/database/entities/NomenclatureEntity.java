package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;

//TODO
@Entity
@Getter
@Setter
@Table(name = "nomenclature")
public class NomenclatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private long amount = 0L;

    public NomenclatureEntity() {
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

    public void setCode(String code) {
        if (Strings.isBlank(code)) {
            this.code = Strings.EMPTY;
        } else {
            this.code = code;
        }
    }
}
