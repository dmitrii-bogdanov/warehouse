package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.services.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//TODO
@Service
public class UserServiceImpl implements UserService {


    //TODO implement
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
