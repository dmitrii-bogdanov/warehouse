package bogdanov.warehouse.handlers;

import bogdanov.warehouse.dto.ExceptionDTO;
import bogdanov.warehouse.exceptions.*;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//TODO Temporary
@RestControllerAdvice
public class OtherExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(
            value = {
                    AlreadyRegisteredPersonException.class,
                    BlankNameException.class,
                    NotAllRequiredFieldsPresentException.class,
                    NullIdException.class,
                    PasswordException.class,
                    UsernameException.class,
                    AlreadyRecordedNameOrCodeException.class,
                    NomenclatureNotEnoughNumberAvailable.class,
                    BlankCodeException.class,
                    InvalidDataAccessApiUsageException.class,
                    IllegalArgumentException.class
            })
    protected ResponseEntity<ExceptionDTO> handleException(RuntimeException e) {
        return new ResponseEntity(
                new ExceptionDTO(e, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    protected ResponseEntity<ExceptionDTO> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity(
                new ExceptionDTO(e),
                HttpStatus.NOT_FOUND
        );
    }

    //TODO Change. Test only for now
    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<ExceptionDTO> handleOther(RuntimeException e) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(e);
        return new ResponseEntity(exceptionDTO, exceptionDTO.getStatus());
    }
}
