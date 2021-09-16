package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.exceptions.ArgumentException;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import bogdanov.warehouse.services.interfaces.PositionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class PersonMapper {

    private final PositionService positionService;
    private final ObjectMapper objectMapper;

    private String toUpperCase(String str) {
        if (Strings.isNotBlank(str)) {
            str =  str.toUpperCase(Locale.ROOT);
        } else {
            str = Strings.EMPTY;
        }
        return str;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (Strings.isBlank(phoneNumber)) {
            return Strings.EMPTY;
        }
            Matcher matcher = Pattern.compile("\\d").matcher(phoneNumber);
            if (!matcher.find()) {
                throw new ArgumentException(ExceptionType.INVALID_PHONE_NUMBER);
            }
            int plusIndex;
            boolean isStartingWithPlus = ((plusIndex = phoneNumber.indexOf('+')) > -1)
                    && (plusIndex < matcher.start());
            phoneNumber = phoneNumber.replaceAll("[\\D]+", Strings.EMPTY);
            return isStartingWithPlus ? ('+' + phoneNumber) : phoneNumber;
    }

    PersonEntity convert(PersonDTO person) {
        return new PersonEntity(
                person.getId(),
                toUpperCase(person.getFirstname()),
                toUpperCase(person.getLastname()),
                toUpperCase(person.getPatronymic()),
                person.getBirth(),
                formatPhoneNumber(person.getPhoneNumber()),
                toUpperCase(person.getEmail()),
                positionService.add(person.getPosition())
        );
    }

    private String getBackOrNull(String str) {
        return Strings.isBlank(str) ? null : str;
    }

    PersonDTO convert(PersonEntity person) {
        return new PersonDTO(
                person.getId(),
                person.getFirstname(),
                person.getLastname(),
                getBackOrNull(person.getPatronymic()),
                person.getBirth(),
                getBackOrNull(person.getPhoneNumber()),
                getBackOrNull(person.getEmail()),
                person.getPosition().getName()
        );
    }


}
