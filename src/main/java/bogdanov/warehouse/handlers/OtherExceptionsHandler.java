package bogdanov.warehouse.handlers;

import bogdanov.warehouse.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class OtherExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(
            value = {
            AlreadyRegisteredPersonException.class,
            BlankNameException.class,
            NotAllRequiredFieldsPresentException.class,
            NullIdException.class,
            PasswordException.class,
            ResourceNotFoundException.class,
            UsernameException.class
    })
    protected ResponseEntity<String> handleException(RuntimeException e) {
        return new ResponseEntity(
                e.getClass().getSimpleName() + " : " + e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

}
