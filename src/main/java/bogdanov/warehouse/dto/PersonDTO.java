package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class PersonDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String phoneNumber;
    private String email;
    private String company;
    private Long StaffId;

}
