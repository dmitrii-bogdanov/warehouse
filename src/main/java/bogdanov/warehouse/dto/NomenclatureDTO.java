package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class NomenclatureDTO {

    private Long id;
    private String name;
    private String code;
    private Long amount;

}
