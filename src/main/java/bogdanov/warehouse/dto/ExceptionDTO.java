package bogdanov.warehouse.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Data
public class ExceptionDTO {

    private String exceptionName;
    private String exceptionMessage;
    @JsonIgnore
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ExceptionDTO(RuntimeException e) {
        setExceptionName(e.getClass().getSimpleName());
        setExceptionMessage(e.getMessage());
    }

    public ExceptionDTO(RuntimeException e, HttpStatus status) {
        this(e);
        this.status = status;
    }

}
