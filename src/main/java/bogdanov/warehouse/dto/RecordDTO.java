package bogdanov.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDTO {

    private Long id;
    private String type;
    private Long amount;
    private Long nomenclatureId;
    private LocalDateTime time;
    private Long userId;

}
