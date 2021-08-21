package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.database.entities.UserEntity;
import bogdanov.warehouse.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

//    UserEntity convert(UserDTO user) {
//        UserEntity userEntity = new UserEntity();
//
//        userEntity.setId(user.getId());
//        userEntity.setUsername(user.getUsername());
//
//        return userEntity;
//    }

    UserDTO convert(UserEntity user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());

        StaffEntity staff = user.getStaff();
        PersonEntity person = staff.getPerson();

        userDTO.setFirstname(person.getFirstname());
        userDTO.setLastname(person.getLastname());
        userDTO.setPatronymic(person.getPatronymic());
        userDTO.setPosition(staff.getPosition().getName());

        return userDTO;
    }

}
