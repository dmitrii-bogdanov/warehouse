package bogdanov.warehouse.handlers;

import bogdanov.warehouse.dto.ExceptionDTO;
import bogdanov.warehouse.exceptions.*;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//TODO Temporary
@Slf4j
@RestControllerAdvice
public class OtherExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(
            value = {
                    InvalidDataAccessApiUsageException.class,
                    IllegalArgumentException.class
            })
    protected ResponseEntity<ExceptionDTO> handleException(RuntimeException e) {
        if (InvalidDataAccessApiUsageException.class.equals(e.getClass())
        && IllegalArgumentException.class.equals(e.getCause().getClass())) {
            e = new ArgumentException(ExceptionType.NULL_ID);
            return handleOther(e);
        }
        return new ResponseEntity<>(
                new ExceptionDTO(e, HttpStatus.BAD_REQUEST),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = WarehouseExeption.class)
    protected ResponseEntity<ExceptionDTO> handleWarehouseException(WarehouseExeption e) {
        return new ResponseEntity<>(new ExceptionDTO(e.getExceptionType()), e.getExceptionType().getStatus());
    }

    //TODO Change. Test only for now
    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<ExceptionDTO> handleOther(RuntimeException e) {
        log.error(e.getClass().getSimpleName() + " : " + e.getMessage(), e);
        ExceptionDTO exceptionDTO = new ExceptionDTO(e);
        return new ResponseEntity(exceptionDTO, exceptionDTO.getStatus());
    }
}
