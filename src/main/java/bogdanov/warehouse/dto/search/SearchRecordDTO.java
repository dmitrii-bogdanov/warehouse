package bogdanov.warehouse.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRecordDTO {

    private String type;
    private List<Long> nomenclatureId;
    private List<Long> userId;
    private LocalDate fromDate;
    private LocalDate toDate;

}
