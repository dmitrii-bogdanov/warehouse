package bogdanov.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Data
@AllArgsConstructor
public class PositionDTO {

    private Long id;
    private String name;

}
