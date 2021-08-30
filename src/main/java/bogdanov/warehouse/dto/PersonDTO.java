package bogdanov.warehouse.dto;

import bogdanov.warehouse.exceptions.NotAllRequiredFieldsPresentException;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    private Long id;
    private String firstname;
    private String patronymic;
    private String lastname;
    private LocalDate birth;
    private String phoneNumber;
    private String email;
    private String position;

    public boolean allRequiredFieldsPresent() {
        if (Strings.isNotBlank(firstname)
                && Strings.isNotBlank(lastname)
                && birth != null
                && Strings.isNotBlank(position)) {
            return true;
        } else {
            throw new NotAllRequiredFieldsPresentException(
                    "Person firstname, lastname and date of birth should be present");
        }
    }

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

    public void setFirstname(String firstname) {
        if (firstname != null) {
            firstname = firstname.toUpperCase(Locale.ROOT);
        }
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        if (lastname != null) {
            lastname = lastname.toUpperCase(Locale.ROOT);
        }
        this.lastname = lastname;
    }

    public void setPatronymic(String patronymic) {
        if (Strings.isNotBlank(patronymic)) {
            patronymic = patronymic.toUpperCase(Locale.ROOT);
        }
        this.patronymic = Strings.EMPTY;
    }

    public void setEmail(String email) {
        if (email != null) {
            email = email.toUpperCase(Locale.ROOT);
        }
        this.email = email;
    }

    public void setPosition(String position) {
        if (position != null) {
            position = position.toUpperCase(Locale.ROOT);
        }
        this.position = position;
    }

}
