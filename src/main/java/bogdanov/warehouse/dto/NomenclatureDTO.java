package bogdanov.warehouse.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class NomenclatureDTO {

    private Long id;
    private String name;
    private String code;
    private Long amount;

    public boolean isEmpty() {
        return id == null
                && name == null
                && code == null
                && amount == null;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public String toFormattedString() {
        return "{\n"
                + "\t\"id\" : " + id + ",\n"
                + "\t\"name\" : \"" + name + "\",\n"
                + "\t\"code\" : \"" + code + "\",\n"
                + "\t\"amount\" : " + amount + "\n"
                + "}";
    }

}
