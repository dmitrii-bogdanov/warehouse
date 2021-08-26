package bogdanov.warehouse.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.util.Locale;

@Data
@NoArgsConstructor
public class UserAccountDTO {

    private Long id;
    private String username;
    private Long personId;
    private String[] roles;

    public void setUsername(String username) {
        if (Strings.isBlank(username)) {
            this.username = Strings.EMPTY;
        }
        this.username = username.toUpperCase(Locale.ROOT);
    }
}
