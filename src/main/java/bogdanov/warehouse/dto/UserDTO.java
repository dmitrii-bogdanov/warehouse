package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String lastname;
    private String firstname;
    private String patronymic;
    private String position;

}
