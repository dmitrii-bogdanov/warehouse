package bogdanov.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor
public class PositionDTO {

    private Long id;
    private String name;

    public PositionDTO(String name) {
        setName(name);
    }

    public void setName(String name) {
        if (name != null) {
            name = name.toUpperCase(Locale.ROOT);
        }
        this.name = name;
    }

}
