package bogdanov.warehouse.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@PropertySource("classpath:bcrypt.properties")
public class BCryptConfig {

    @Bean("user")
    public BCryptPasswordEncoder getBCryptPasswordEncoderForUser(@Value("${adminStr}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean("admin")
    public BCryptPasswordEncoder getBCryptPasswordEncoderForAdmin(@Value("${adminStr") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

}
