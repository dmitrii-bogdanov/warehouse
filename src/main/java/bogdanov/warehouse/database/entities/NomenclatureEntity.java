package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//TODO
@Entity
@Getter
@Setter
@Table(name = "nomenclature")
public class NomenclatureEntity {

    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private long amount = 0L;

    @Column(nullable = false)
    private boolean isActive = true;

    NomenclatureEntity() {
    }

    public NomenclatureEntity(long id, String name, String code, long amount) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.amount = amount;
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

}
