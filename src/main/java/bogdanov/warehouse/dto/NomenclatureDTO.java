package bogdanov.warehouse.dto;

import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.util.Locale;

@Data
@NoArgsConstructor
public class NomenclatureDTO {

    private Long id;
    private String name;
    private String code;
    private Long amount;

    public NomenclatureDTO(NomenclatureDTO dto) {
        this.id = dto.id;
        this.name = dto.name;
        this.code = dto.code;
        this.amount = dto.amount;
    }

    public NomenclatureDTO(Long id, String name, String code, Long amount) {
        this.id = id;
        this.amount = amount;
        setName(name);
        setCode(code);
    }

    public boolean isEmpty() {
        return id == null
                && name == null
                && code == null
                && amount == null;
    }

    public void setName(String name) {
        if (Strings.isNotBlank(name)) {
            this.name = name.toUpperCase(Locale.ROOT);
        } else {
            this.name = Strings.EMPTY;
        }
    }

    public void setCode(String code) {
        if (Strings.isNotBlank(code)) {
            this.code = code.toUpperCase(Locale.ROOT);
        } else {
            this.code = Strings.EMPTY;
        }
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public String toFormattedString() {
        return "\n{\n"
                + "\t\"id\" : " + id + ",\n"
                + "\t\"name\" : \"" + name + "\",\n"
                + "\t\"code\" : \"" + code + "\",\n"
                + "\t\"amount\" : " + amount + "\n"
                + "}";
    }

}
