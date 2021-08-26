package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.UserAccountDTO;
import bogdanov.warehouse.dto.UserAccountWithPasswordDTO;
import bogdanov.warehouse.services.interfaces.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/console/accounts",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class AccountRestController {

    private final UserAccountService accountService;

    @GetMapping
    public List<UserAccountDTO> getAll() {
        return accountService.getAll();
    }

    @PostMapping
    public UserAccountDTO register(@RequestBody UserAccountWithPasswordDTO user) {
        //TODO delete
        log.info(Arrays.toString(user.getRoles()));

        return accountService.add(user);
    }

    @GetMapping(params = "id")
    public UserAccountDTO getById(@RequestParam Long id) {
        return accountService.getById(id);
    }

    @GetMapping(params = "username")
    public UserAccountDTO getByUsername(@RequestParam String username) {
        return accountService.getByUsername(username);
    }

    @GetMapping(params = "personId")
    public UserAccountDTO getByPersonId(@RequestParam Long personId) {
        return accountService.getByPersonId(personId);
    }

    @GetMapping(params = "role")
    public List<UserAccountDTO> getAllByRole(@RequestParam String role) {
        return accountService.findAllByRole(role);
    }

    @PutMapping(params = "password")
    public UserAccountDTO updatePassword(@RequestBody UserAccountWithPasswordDTO user) {
        return accountService.updatePassword(user);
    }

    @PutMapping(params = "roles")
    public UserAccountDTO updateRoles(@RequestBody UserAccountDTO user) {
        return accountService.updateRoles(user);
    }

    @PutMapping(params = "username")
    public UserAccountDTO updateUsername(@RequestBody UserAccountDTO user) {
        return accountService.updateUsername(user);
    }

    @PutMapping(params = "enable")
    public UserAccountDTO enable(@RequestBody UserAccountDTO user) {
        return accountService.enable(user);
    }

    @PutMapping(params = "disable")
    public UserAccountDTO disable(@RequestBody UserAccountDTO user) {
        return accountService.disable(user);
    }

}
