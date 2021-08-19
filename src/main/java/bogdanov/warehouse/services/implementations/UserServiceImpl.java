package bogdanov.warehouse.services.implementations;

import bogdanov.warehouse.database.repositories.StaffRepository;
import bogdanov.warehouse.database.repositories.UserRepository;
import bogdanov.warehouse.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//TODO
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private StaffRepository staffRepository;

    //region Autowired setters
    @Autowired
    private void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private void setStaffRepository(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
    //endregion



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
}
