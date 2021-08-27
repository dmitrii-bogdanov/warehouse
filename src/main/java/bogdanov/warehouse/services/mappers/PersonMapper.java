package bogdanov.warehouse.services.mappers;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.services.interfaces.PositionService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PersonMapper {

    private final PositionService positionService;
    private final PositionMapper positionMapper;

    PersonEntity convert(PersonDTO person) {
        return new PersonEntity(
                person.getId(),
                person.getFirstname(),
                person.getLastname(),
                Strings.isBlank(person.getPatronymic())
                        ? Strings.EMPTY : person.getPatronymic(),
                person.getBirth(),
                person.getPhoneNumber(),
                person.getEmail(),
                positionMapper.convert(positionService.add(person.getPosition()))
        );
    }

    PersonDTO convert(PersonEntity person) {
        return new PersonDTO(
                person.getId(),
                person.getFirstname(),
                person.getLastname(),
                person.getPatronymic(),
                person.getBirth(),
                person.getPhoneNumber(),
                person.getEmail(),
                person.getPosition().getName()
        );
    }


}
