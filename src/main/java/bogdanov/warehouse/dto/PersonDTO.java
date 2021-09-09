package bogdanov.warehouse.dto;

import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.util.Locale;

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

    public boolean allRequiredFieldsPresent() {
        if (Strings.isNotBlank(firstname)
                && Strings.isNotBlank(lastname)
                && birth != null
                && Strings.isNotBlank(position)) {
            return true;
        } else {
            throw new IllegalArgumentException(ExceptionMessage.NOT_ALL_PERSON_REQUIRED_FIELDS.getMessage());
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
            this.patronymic = patronymic.toUpperCase(Locale.ROOT);
        } else {
            this.patronymic = Strings.EMPTY;
        }
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
