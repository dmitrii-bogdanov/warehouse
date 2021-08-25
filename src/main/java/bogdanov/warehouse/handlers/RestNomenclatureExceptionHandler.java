package bogdanov.warehouse.handlers;

import bogdanov.warehouse.exceptions.NomenclatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestNomenclatureExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NomenclatureException.class)
    protected ResponseEntity<NomenclatureException> handleExcepsion(RuntimeException e) {
        return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
    }


}
