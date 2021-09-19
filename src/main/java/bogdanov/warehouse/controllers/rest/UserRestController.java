package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.UserDTO;
import bogdanov.warehouse.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/users",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @GetMapping(params = "username")
    public UserDTO getByUsername(@RequestParam String username) {
         return userService.getByUsername(username);
    }

    @GetMapping(params = "personId")
    public UserDTO getByPersonId(@RequestParam Long personId) {
        return userService.getByPersonId(personId);
    }

    @GetMapping(params = "position")
    public List<UserDTO> getAllByPosition(@RequestParam String position) {
        return userService.findAllByPosition(position);
    }

    @GetMapping(params = {"search","firstname", "patronymic", "lastname"})
    public List<UserDTO> getAllByFullName(@RequestParam String firstname,
                                          @RequestParam String patronymic,
                                          @RequestParam String lastname) {
        return userService.findAllByFullName(firstname, patronymic, lastname);
    }

}
