package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserAccountDTO {

    private Long id;
    private String username;
    private String password;
    private Long personId;
    private String[] roles;

}
