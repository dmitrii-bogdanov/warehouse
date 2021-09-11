package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.services.interfaces.PositionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class PersonMapper {

    private final PositionService positionService;
    private final ObjectMapper objectMapper;

    private String toUpperCase(String str) {
        if (Strings.isNotBlank(str)) {
            str =  str.toUpperCase(Locale.ROOT);
        } else {
            str = null;
        }
        return str;
    }

    PersonEntity convert(PersonDTO person) {
        return new PersonEntity(
                person.getId(),
                toUpperCase(person.getFirstname()),
                toUpperCase(person.getLastname()),
                toUpperCase(person.getPatronymic()),
                person.getBirth(),
                person.getPhoneNumber(),
                toUpperCase(person.getEmail()),
                positionService.add(person.getPosition())
        );
    }

    PersonDTO convert(PersonEntity person) {
        return objectMapper.convertValue(person, PersonDTO.class);
//        return new PersonDTO(
//                person.getId(),
//                person.getFirstname(),
//                person.getLastname(),
//                person.getPatronymic(),
//                person.getBirth(),
//                person.getPhoneNumber(),
//                person.getEmail(),
//                person.getPosition().getName()
//        );
    }


}
