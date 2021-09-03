package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.RecordDTO;
import bogdanov.warehouse.dto.RecordInputDTO;
import bogdanov.warehouse.dto.RecordOutputDTO;
import bogdanov.warehouse.dto.ReverseRecordDTO;
import bogdanov.warehouse.services.interfaces.RecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/records",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class RecordRestController {

    private final RecordService recordService;

    @GetMapping
    public List<RecordDTO> getAll() {
        return recordService.getAll();
    }

    @GetMapping("/{id}")
    public RecordDTO getById(@PathVariable Long id) {
        return recordService.getById(id);
    }

    @PostMapping
    public RecordDTO addRecord(@RequestBody RecordInputDTO record, Principal user) {
        return recordService.add(record, user.getName());
    }

    @DeleteMapping("/{id}")
    public RecordDTO revertRecord(@PathVariable Long id, Principal user) {
        return recordService.revert(id, user.getName());
    }

    @PutMapping("/{id}")
    public RecordDTO updateRecord(@PathVariable Long id, Principal user, @RequestBody RecordInputDTO record) {
        return recordService.update(id, user.getName(), record);
    }

    @GetMapping("/reception")
    public List<RecordDTO> getAllReceptionRecords() {
        return recordService.findAllByType("RECEPTION");
    }

    @GetMapping("/release")
    public List<RecordDTO> getAllReleaseRecords() {
        return recordService.findAllByType("RELEASE");
    }

    @GetMapping("/deleted")
    public List<RecordDTO> getAllDeletedRecords() {
        return recordService.findAllByType("DELETED");
    }

    @GetMapping(params = "type")
    public List<RecordDTO> getAllByType(@RequestParam String type) {
        return recordService.findAllByType(type.toUpperCase(Locale.ROOT));
    }

    @GetMapping(params = "userId")
    public List<RecordDTO> getAllRecordsByUserId(@RequestParam Long userId) {
        return recordService.findAllByUserId(userId);
    }

    @GetMapping(params = "username")
    public List<RecordDTO> getAllRecordsByUserUsername(@RequestParam String username) {
        return recordService.findAllByUserUsername(username);
    }

    @GetMapping("/my")
    public List<RecordDTO> getCurrentUserRecords(Principal user) {
        return recordService.findAllByUserUsername(user.getName());
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

    @GetMapping(params = "date")
    public List<RecordDTO> getAllRecordByDate(@RequestParam LocalDate date) {
        return recordService.findAllByDate(date);
    }

    @GetMapping(params = {"date", "start", "end"})
    public List<RecordDTO> getAllRecordsByDateBetween(@RequestParam LocalDate start,
                                                      @RequestParam LocalDate end) {
        return recordService.findAllByDateBetween(start, end);
    }

    @GetMapping("/today")
    public List<RecordDTO> getAllRecordsForToday() {
        return recordService.findAllByDate(LocalDate.now());
    }

    @GetMapping("/reverse")
    public List<ReverseRecordDTO> getAllReverseRecords() {
        return recordService.getAllReverseRecords();
    }
}
