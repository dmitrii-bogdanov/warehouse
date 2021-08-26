package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.RoleDTO;
import bogdanov.warehouse.services.interfaces.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/console/roles",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class RoleRestController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleDTO> getAll() {
        return roleService.getAll();
    }

}
