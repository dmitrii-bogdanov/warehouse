package bogdanov.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReverseRecordDTO {

    private Long id;
    private Long revertedRecordId;
    private Long generatedRecordId;
    private LocalDateTime time;
    private Long userId;

}
