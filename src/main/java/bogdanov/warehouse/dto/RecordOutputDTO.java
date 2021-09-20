package bogdanov.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordOutputDTO extends RecordDTO{

    private Long nomenclatureId;
    private LocalDateTime time;
    private Long userId;

}
