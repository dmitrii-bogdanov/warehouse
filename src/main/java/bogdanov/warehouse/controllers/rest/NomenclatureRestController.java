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

    @GetMapping("/{id}")
    public NomenclatureDTO getById(@PathVariable Long id) {
        return nomenclatureService.getById(id);
    }

    @PostMapping
    public List<NomenclatureDTO> createNew(@RequestBody List<NomenclatureDTO> nomenclature) {
        return nomenclatureService.createNew(nomenclature);
    }

    @PutMapping
    public List<NomenclatureDTO> update(@RequestBody List<NomenclatureDTO> nomenclature) {
        return nomenclatureService.update(nomenclature);
    }

    @GetMapping("/available")
    public List<NomenclatureDTO> getAllAvailable() {
        return nomenclatureService.getAllAvailable();
    }

    @GetMapping(params = "name")
    public NomenclatureDTO getByName(@RequestParam String name) {
        return nomenclatureService.getByName(name);
    }

    @GetMapping(params = "code")
    public NomenclatureDTO getByCode(@RequestParam String code) {
        return nomenclatureService.getByCode(code);
    }

    @GetMapping(value = "/search", params = {"name", "code", "minAmount", "maxAmount"})
    public List<NomenclatureDTO> findAllByNameContainingAndCodeContaining(@RequestParam(required = false) String name,
                                                                          @RequestParam(required = false) String code,
                                                                          @RequestParam(required = false) Long minAmount,
                                                                          @RequestParam(required = false) Long maxAmount) {
        return nomenclatureService.search(name, code, minAmount, maxAmount);
    }

}
