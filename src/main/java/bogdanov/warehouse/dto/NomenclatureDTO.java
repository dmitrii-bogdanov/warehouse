package bogdanov.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
