package bogdanov.warehouse.dto;

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
        return Strings.isNotBlank(firstname)
                && Strings.isNotBlank(lastname)
                && birth != null;
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
        if (patronymic != null) {
            patronymic = patronymic.toUpperCase(Locale.ROOT);
        }
        this.patronymic = patronymic;
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
