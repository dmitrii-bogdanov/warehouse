package bogdanov.warehouse.dto;

import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountDTO {

    private Long id;
    private String username;
    private Long personId;
    private Collection<String> roles;
    private Boolean isEnabled;

}
