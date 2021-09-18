package bogdanov.warehouse.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountWithPasswordDTO extends UserAccountDTO {

    private String password;

}
