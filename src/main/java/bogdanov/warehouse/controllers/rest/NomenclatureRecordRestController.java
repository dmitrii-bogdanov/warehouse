package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.services.interfaces.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

//TODO is this controller needed

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/nomenclature/",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class NomenclatureRecordRestController {

    private final RecordService recordService;

    @GetMapping("{id}/records")
    public List<RecordDTO> getAll(@PathVariable Long id) {
        return recordService.findAllByNomenclatureId(id);
    }

    //TODO ???
    @PostMapping("{id}/records")
    public RecordDTO add(@PathVariable Long id, @RequestBody RecordDTO record) {
        return recordService.add(Long id, record);
    }

    @GetMapping("{id}/records/{recordId}")
    public RecordDTO getById(@PathVariable Long id, @PathVariable Long recordId) {
        return recordService.getByIdAndNomenclatureId(recordId, id);
    }

    @GetMapping("{id}/records/my")
    public List<RecordDTO> getAllCurrentUserRecords(@PathVariable Long id, Principal user) {
        return recordService.findAllByNomenclatureIdAndUserUsername(id, user.getName());
    }

    @GetMapping(value = "{id}/records", params = "userId")
    public List<RecordDTO> getAllRecordsByUserId(@PathVariable Long id, @RequestParam Long userId) {
        return recordService.findAllByNomenclatureIdAndUserId(id, userId);
    }

    @GetMapping(value = "{id}/records", params = "username")
    public List<RecordDTO> getAllRecordsByUserUsername(@PathVariable Long id, @RequestParam String username) {
        return recordService.findAllByNomenclatureIdAndUserUsername(id, username);
    }

    @GetMapping(value = "{id}/records/reception")
    public List<RecordDTO> getAllReceptionRecords(@PathVariable Long id) {
        return recordService.findAllByNomenclatureIdAndType(id, "RECEPTION");
    }

    @GetMapping(value = "{id}/records/release")
    public List<RecordDTO> getAllReleaseRecords(@PathVariable Long id) {
        return recordService.findAllByNomenclatureIdAndType(id, "RELEASE");
    }

    @GetMapping(value = "{id}/records", params = "type")
    public List<RecordDTO> getAllReleaseRecords(@PathVariable Long id, @RequestParam String type) {
        return recordService.findAllByNomenclatureIdAndType(id, type.toUpperCase(Locale.ROOT));
    }


}
