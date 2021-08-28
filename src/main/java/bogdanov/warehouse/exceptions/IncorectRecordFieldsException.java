package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class IncorectRecordFieldsException extends RuntimeException{

    public IncorectRecordFieldsException() {
        super();
    }

    public IncorectRecordFieldsException(String message) {
        super(message);
    }

}
