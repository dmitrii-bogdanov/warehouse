package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/nomenclature",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class NomenclatureRestController {

    private final NomenclatureService nomenclatureService;

    @GetMapping
    public List<NomenclatureDTO> getAll() {
        return nomenclatureService.getAll();
    }

    @GetMapping(params = "id")
    public NomenclatureDTO getById(@RequestParam("id") Long id) {
        return nomenclatureService.getById(id);
    }

    @PostMapping
    public List<NomenclatureDTO> createNew(@RequestBody List<NomenclatureDTO> nomenclature) {
        return nomenclatureService.createNew(nomenclature);
    }

    @GetMapping(params = "name")
    public NomenclatureDTO getByName(@RequestParam("name") String name) {
        return nomenclatureService.getByName(name);
    }

    @GetMapping(params = "code")
    public NomenclatureDTO getByCode(@RequestParam("code") String code) {
        return nomenclatureService.getByCode(code);
    }

    @GetMapping(value = "/search", params = "name")
    public List<NomenclatureDTO> findAllByNameContaining(@RequestParam("name") String name) {
        return nomenclatureService.findAllByNameContaining(name);
    }

    @GetMapping(value = "/search", params = "code")
    public List<NomenclatureDTO> findAllByCodeContaining(@RequestParam("code") String code) {
        return nomenclatureService.findAllByCodeContaining(code);
    }

}
