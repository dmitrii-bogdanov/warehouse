package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.dto.PersonDTO;
import org.springframework.stereotype.Component;

@Component
public class PersonMapper {
    PersonEntity convert(PersonDTO person) {
        PersonEntity personEntity = new PersonEntity();

        personEntity.setFirstname(person.getFirstname());
        personEntity.setLastname(person.getLastname());
        personEntity.setPatronymic(person.getPatronymic());
        personEntity.setBirth(person.getBirth());
        personEntity.setPhoneNumber(person.getPhoneNumber());
        personEntity.setEmail(person.getEmail());

        return personEntity;
    }

    PersonDTO convert(PersonEntity person) {
        PersonDTO personDTO = new PersonDTO();

        personDTO.setId(person.getId());
        personDTO.setFirstname(person.getFirstname());
        personDTO.setLastname(person.getLastname());
        personDTO.setPatronymic(person.getPatronymic());
        personDTO.setBirth(person.getBirth());
        personDTO.setPhoneNumber(person.getPhoneNumber());
        personDTO.setEmail(person.getEmail());
        personDTO.setPosition(person.getPosition().getName());

        return personDTO;
    }

//    PersonEntity update(PersonDTO person) {
//        PersonEntity personEntity = new PersonEntity();
//
//        if (person.getFirstname().isBlank()) {
//            personEntity.setFirstname(person.getFirstname());
//        }
//        if (person.getLastname().isBlank()) {
//            personEntity.setLastname(person.getLastname());
//        }
//        if (person.getPatronymic().isBlank()) {
//            personEntity.setPatronymic(person.getPatronymic());
//        }
//        if (person.getPhoneNumber() != null) {
//            personEntity.setPhoneNumber(person.getPhoneNumber());
//        }
//        if (person.getEmail() != null) {
//            personEntity.setEmail(person.getEmail());
//        }
//
//        return personEntity;
//    }

}
