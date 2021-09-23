package bogdanov.warehouse.handlers;

import bogdanov.warehouse.AbstractSpringBootTest;
import bogdanov.warehouse.dto.ExceptionDTO;
import bogdanov.warehouse.exceptions.IntervalServerException;
import bogdanov.warehouse.exceptions.WarehouseExeption;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class OtherExceptionsHandlerTest extends AbstractSpringBootTest {

    @Autowired
    private OtherExceptionsHandler handler;

    private final WarehouseExeption INTERNAL_SERVER_EXCEPTION =
            new IntervalServerException(ExceptionType.INTERNAL_SERVER_ERROR);

    @Test
    void internalServerException() {
        ExceptionDTO e = new ExceptionDTO(INTERNAL_SERVER_EXCEPTION);
        ResponseEntity<ExceptionDTO> responseEntity =
                new ResponseEntity<>(e, e.getStatus());

        assertEquals(responseEntity, handler.handleOther(new RuntimeException()));
    }

}