package bogdanov.warehouse.configs;

import bogdanov.warehouse.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests().antMatchers("/**").permitAll()
                .and().formLogin();
    }

    @Autowired
    private void configureGlobal(AuthenticationManagerBuilder auth,
                                 UserService userService,
                                 @Qualifier("user") BCryptPasswordEncoder encoder) throws Exception{
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }

}
