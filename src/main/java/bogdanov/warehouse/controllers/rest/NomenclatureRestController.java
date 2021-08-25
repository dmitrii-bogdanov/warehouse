package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.NomenclatureDTO;
import bogdanov.warehouse.services.interfaces.NomenclatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController("/nomenclature")
public class NomenclatureRestController {

    private final NomenclatureService nomenclatureService;

    @GetMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<NomenclatureDTO> getAll() {
        return nomenclatureService.getAll();
    }

    @GetMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public NomenclatureDTO getById(@RequestParam("id") Long id) {
        return nomenclatureService.getById(id);
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public NomenclatureDTO createNew(@RequestBody NomenclatureDTO nomenclature) {
        return nomenclatureService.createNew(nomenclature);
    }

    @GetMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public NomenclatureDTO getByName(@RequestParam("name") String name) {
        return nomenclatureService.getByName(name);
    }

    @GetMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public NomenclatureDTO getByCode(@RequestParam("code") String code) {
        return nomenclatureService.getByCode(code);
    }

    @GetMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<NomenclatureDTO> findAllByNameContaining(@RequestParam("name") String name) {
        return nomenclatureService.findAllByNameContaining(name);
    }

    @GetMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<NomenclatureDTO> findAllByCodeContaining(@RequestParam("code") String code) {
        return nomenclatureService.findAllByCodeContaining(code);
    }


}
