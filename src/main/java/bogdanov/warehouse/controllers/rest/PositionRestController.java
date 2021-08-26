package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.PositionDTO;
import bogdanov.warehouse.services.interfaces.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/positions",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class PositionRestController {

    private final PositionService positionService;

    @GetMapping
    public List<PositionDTO> getAll() {
        return positionService.getAll();
    }

    //TODO not working can't find List public constructor
    @PostMapping
    public List<PositionDTO> add(@RequestBody List<PositionDTO> positions) {
        return positionService.add(positions);
    }

    @PutMapping
    public List<PositionDTO> update(@RequestBody List<PositionDTO> positions) {
        return positionService.update(positions);
    }

    @GetMapping(params = "id")
    public PositionDTO getById(@RequestParam Long id) {
        return positionService.getById(id);
    }

    @GetMapping(params = "name")
    public PositionDTO getByName(@RequestParam String name) {
        return positionService.getByName(name);
    }

    @GetMapping(value = "/find", params = "name")
    public List<PositionDTO> findAllByNameContaining(@RequestParam String name) {
        return positionService.findAllByNameContaining(name);
    }

}