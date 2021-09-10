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

    public PersonDTO(PersonDTO dto) {
        this.id = dto.id;
        this.firstname = dto.firstname;
        this.lastname = dto.lastname;
        this.patronymic = dto.patronymic;
        this.birth = dto.birth;
        this.position = dto.position;
        this.phoneNumber = dto.phoneNumber;
        this.email = dto.email;
    }

}
