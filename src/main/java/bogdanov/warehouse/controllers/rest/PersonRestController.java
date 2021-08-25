package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.services.interfaces.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/persons",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class PersonRestController {

    private final PersonService personService;

    @GetMapping
    public List<PersonDTO> getAll() {
        return personService.getAll();
    }

    @PostMapping
    public List<PersonDTO> add(List<PersonDTO> persons) {
        return personService.add(persons);
    }

    @PutMapping
    public List<PersonDTO> update(List<PersonDTO> persons) {
        return personService.update(persons);
    }

    @GetMapping(params = "firstname")
    public List<PersonDTO> findAllByFirstname(@RequestParam String firstname) {
        return personService.findAllByFirstname(firstname);
    }

    @GetMapping(params = "lastname")
    public List<PersonDTO> findAllByLastname(@RequestParam String lastname) {
        return personService.findAllByLastname(lastname);
    }

    @GetMapping(params = "patronymic")
    public List<PersonDTO> findAllByPatronymic(@RequestParam String patronymic) {
        return personService.findAllByPatronymic(patronymic);
    }

    @GetMapping(params = {"firstname", "patronymic", "lastname"})
    public List<PersonDTO> findAllByFullName(@RequestParam String firstname,
                                             @RequestParam String patronymic,
                                             @RequestParam String lastname) {
        return personService.findAllByFullName(firstname, patronymic, lastname);
    }

}
