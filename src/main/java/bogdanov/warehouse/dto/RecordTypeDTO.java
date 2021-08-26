package bogdanov.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class RecordTypeDTO {

    private Long id;
    private String name;

    public void setName(String name) {
        this.name = name.toUpperCase(Locale.ROOT);
    }

}
