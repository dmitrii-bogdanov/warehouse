package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.services.interfaces.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/nomenclature/records",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class RecordRestController {

    private final RecordService recordService;

    @GetMapping
    public List<RecordDTO> getAll() {
        return recordService.getAll();
    }

    //TODO add User
    @PostMapping
    public RecordDTO addRecord(@RequestBody RecordInputDTO record) {
        return recordService.add(record, null);
    }

    @GetMapping(params = "reception")
    public List<RecordDTO> getAllReceptionRecords() {
        return recordService.findAllByType("RECEPTION");
    }

    @GetMapping(params = "release")
    public List<RecordDTO> getAllReleaseRecords() {
        return recordService.findAllByType("RELEASE");
    }

    @GetMapping(params = "type")
    public List<RecordDTO> getAllByType(@RequestParam String type) {
        return recordService.findAllByType(type.toUpperCase(Locale.ROOT));
    }

    @GetMapping(params = "userId")
    public List<RecordDTO> getAllRecordsByUserId(@RequestParam Long userId) {
        return recordService.findAllByUserId(userId);
    }

    @GetMapping(params = "nomenclatureId")
    public List<RecordDTO> getAllRecordsByNomenclatureId(@RequestParam Long nomenclatureId) {
        return recordService.findAllByNomenclatureId(nomenclatureId);
    }

    @GetMapping(params = "nomenclatureName")
    public List<RecordDTO> getAllRecordsByNomenclatureName(@RequestParam String nomenclatureName) {
        return recordService.findAllByNomenclatureName(nomenclatureName);
    }

    @GetMapping(params = "nomenclatureCode")
    public List<RecordDTO> getAllRecordsByNomenclatureCode(@RequestParam String nomenclatureCode) {
        return recordService.findAllByNomenclatureCode(nomenclatureCode);
    }

}
