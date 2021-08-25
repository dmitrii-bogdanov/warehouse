package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PersonDTO {

    private Long id;
    private String firstname;
    private String patronymic;
    private String lastname;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String position;

}
