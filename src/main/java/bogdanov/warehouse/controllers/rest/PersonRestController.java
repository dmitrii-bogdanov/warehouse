package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.services.interfaces.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/persons",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class PersonRestController {

    private final PersonService personService;

    @GetMapping
    public List<PersonDTO> getAll() {
        return personService.getAll();
    }

    @GetMapping("/{id}")
    public PersonDTO getById(@PathVariable Long id) {
        return personService.getById(id);
    }

    @PostMapping
    public List<PersonDTO> add(@RequestBody List<PersonDTO> persons) {
        return personService.add(persons);
    }

    @PutMapping
    public List<PersonDTO> update(@RequestBody List<PersonDTO> persons) {
        return personService.update(persons);
    }

    @DeleteMapping("/{id}")
    public PersonDTO delete(@PathVariable Long id) {
        return personService.delete(id);
    }

    @GetMapping(
            params = {
                    "search",
                    "firstname",
                    "lastname",
                    "patronymic",
                    "position",
                    "phoneNumber",
                    "email",
                    "startDate",
                    "endDate"
            }
            )
    public List<PersonDTO> search(@RequestParam String firstname,
                                  @RequestParam String lastname,
                                  @RequestParam String patronymic,
                                  @RequestParam String position,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String email,
                                  @RequestParam LocalDate startDate,
                                  @RequestParam LocalDate endDate) {
        return personService.search(firstname, lastname, patronymic, position, phoneNumber, email, startDate, endDate);
    }

}
