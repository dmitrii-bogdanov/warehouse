package bogdanov.warehouse.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPersonDTO {

    private String firstname;
    private String lastname;
    private String patronymic;
    private List<Long> positions;
    private String phoneNumber;
    private String email;
    private LocalDate fromDate;
    private LocalDate toDate;

}
