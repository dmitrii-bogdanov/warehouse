package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class UserAccountDTO {

    private Long id;
    private String username;
    private Long personId;
    private String[] roles;

}
