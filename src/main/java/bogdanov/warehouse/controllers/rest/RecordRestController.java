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

    @GetMapping(params = {"search", "nomenclatureId", "userId", "type", "dateFrom", "dateTo"})
    public List<RecordDTO> updateRecord(@RequestParam Long nomenclatureId,
                                        @RequestParam Long userId,
                                        @RequestParam String type,
                                        @RequestParam LocalDate dateFrom,
                                        @RequestParam LocalDate dateTo) {
        return recordService.search(nomenclatureId, userId, type, dateFrom, dateTo);
    }

    @GetMapping("/reversed")
    public List<ReverseRecordDTO> getAllReverseRecords() {
        return recordService.getAllReverseRecords();
    }
}
