package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDTO {

    private Long id;
    private String username;
    private String password;
    private Long staffId;
    private String[] roles;

}
