package bogdanov.warehouse.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String patronymic;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String position;

}
