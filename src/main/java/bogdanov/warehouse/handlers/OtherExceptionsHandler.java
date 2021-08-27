package bogdanov.warehouse.handlers;

import bogdanov.warehouse.exceptions.*;
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
                    BlankCodeException.class
            })
    protected ResponseEntity<String> handleException(RuntimeException e) {
        return new ResponseEntity(
                e.getClass().getSimpleName() + " : " + e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    protected ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity(
                e.getClass().getSimpleName() + " : " + e.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }
}
