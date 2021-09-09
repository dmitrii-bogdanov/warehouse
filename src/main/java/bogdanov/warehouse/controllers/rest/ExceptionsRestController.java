package bogdanov.warehouse.controllers.rest;

import bogdanov.warehouse.dto.ListOfExceptionMessagesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        value = "/api/exceptions",
//        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
@ResponseStatus(HttpStatus.OK)
public class ExceptionsRestController {

    //TODO Make work
    @GetMapping
    public ListOfExceptionMessagesDTO getList() {
        return new ListOfExceptionMessagesDTO();
    }

}
