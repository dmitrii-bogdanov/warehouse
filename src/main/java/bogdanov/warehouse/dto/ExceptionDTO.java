package bogdanov.warehouse.dto;

import bogdanov.warehouse.exceptions.WarehouseExeption;
import bogdanov.warehouse.exceptions.enums.ExceptionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ExceptionDTO {

    private String exception;
    private String type;
    private String message;
    @JsonIgnore
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ExceptionDTO(RuntimeException e) {
        setException(e.getClass().getSimpleName());
        setMessage(e.getMessage());
    }

    public ExceptionDTO(RuntimeException e, HttpStatus status) {
        this(e);
        this.status = status;
    }

    public ExceptionDTO(ExceptionType e) {
        setMessage(e.getModifiedMessage());
        setStatus(e.getStatus());
        setType(e.name());
    }

    public ExceptionDTO(WarehouseExeption e) {
        setException(e.getClass().getSimpleName());
        setMessage(e.getMessage());
        setType(e.getExceptionType().name());
        setStatus(e.getExceptionType().getStatus());
    }

}
