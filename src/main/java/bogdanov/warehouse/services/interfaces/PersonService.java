package bogdanov.warehouse.services.interfaces;

import bogdanov.warehouse.database.entities.PersonEntity;
import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.search.SearchPersonDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface PersonService {

    List<PersonDTO> add(List<PersonDTO> persons);

    List<PersonDTO> update(List<PersonDTO> person);

    PersonDTO delete(Long id);

    List<PersonDTO> getAll();

    PersonDTO getById(Long id);

    PersonEntity getEntityById(Long id);

    List<PersonDTO> search(SearchPersonDTO dto);

    List<PersonDTO> findAllByPosition(Long id);

    List<PersonDTO> findAllByPosition(String position);

}
