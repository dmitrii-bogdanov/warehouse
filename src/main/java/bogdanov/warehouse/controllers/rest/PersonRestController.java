package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.PersonDTO;
import bogdanov.warehouse.dto.search.SearchPersonDTO;
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

    @GetMapping("/position/{id}")
    public List<PersonDTO> getByPositionId(@PathVariable Long id) {
        return personService.findAllByPosition(id);
    }

    @GetMapping(value = "/position", params = "name")
    public List<PersonDTO> getByPositionName(@RequestParam String name) {
        return personService.findAllByPosition(name);
    }

    @GetMapping(value = "/search")
    public List<PersonDTO> search(@RequestBody SearchPersonDTO searchPersonDto) {
        return personService.search(searchPersonDto);
    }

}
