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
//TODO move wrappers for DataIntegrityViolationException here from services
public class OtherExceptionsHandler extends ResponseEntityExceptionHandler {

    private static final WarehouseExeption INTERNAL_SERVER_EXCEPTION =
            new IntervalServerException(ExceptionType.INTERNAL_SERVER_ERROR);

    @ExceptionHandler(value = WarehouseExeption.class)
    protected ResponseEntity<ExceptionDTO> handleWarehouseException(WarehouseExeption e) {
        ExceptionDTO dto = new ExceptionDTO(e);
        return new ResponseEntity<>(dto, dto.getStatus());
    }

    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<ExceptionDTO> handleOther(RuntimeException e) {
        log.error(e.getClass().getSimpleName() + " : " + e.getMessage(), e);
        return handleWarehouseException(INTERNAL_SERVER_EXCEPTION);
    }
}
