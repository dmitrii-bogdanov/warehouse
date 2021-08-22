package bogdanov.warehouse.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class NomenclatureDTO {

    private Long id;
    private String name;
    private String code;
    private Long amount;

    public String toFormatedString() {
        return "{\n"
                + "\t\"id\" : " + id + ",\n"
                + "\t\"name\" : \"" + name + "\",\n"
                + "\t\"code\" : \"" + code + "\",\n"
                + "\t\"amount\" : " + amount + "\n"
                + "}";
    }

}
