package bogdanov.warehouse.configs;

import bogdanov.warehouse.services.implementations.InternalUserService;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import bogdanov.warehouse.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Method;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //TODO change in accordance with new controllers setup
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests().antMatchers("/**").permitAll();

        httpSecurity.headers().frameOptions().disable();

        //work configuration
//        httpSecurity
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers("/api/console/**").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST, "/api/nomenclature").hasRole("STAFF")
//                .antMatchers(HttpMethod.GET, "/api/nomenclature").hasAnyRole("STAFF", "USER")
//                .antMatchers("/api/persons").hasAnyRole("ADMIN", "STAFF")
//                .antMatchers("/api/positions").hasRole("STAFF")
//                .antMatchers("/api/users").hasRole("STAFF")
//                .antMatchers(HttpMethod.POST, "/api/nomenclature/records").hasAnyRole("STAFF", "USER")
//                .antMatchers(HttpMethod.GET, "/api/nomenclature/records?my").hasAnyRole("STAFF", "USER")
//                .antMatchers(HttpMethod.GET, "/api/nomenclature/records").hasRole("STAFF")
//                .antMatchers(HttpMethod.DELETE, "/api/nomenclature/records").hasRole("STAFF")
//                .and()
//                .httpBasic();

    }

    @Autowired
    private void configureGlobal(AuthenticationManagerBuilder auth,
                                 InternalUserService userService,
                                 @Qualifier("user") BCryptPasswordEncoder encoder) throws Exception{
        auth.userDetailsService(userService).passwordEncoder(encoder);
    }

}
