package bogdanov.warehouse.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class NomenclatureDTO {

    private Long id;
    private String name;
    private String code;
    private Long amount;

}
