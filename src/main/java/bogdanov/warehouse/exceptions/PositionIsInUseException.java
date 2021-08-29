package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class PositionIsInUseException extends RuntimeException{

    public PositionIsInUseException() {
        super();
    }

    public PositionIsInUseException(String message) {
        super(message);
    }

    public PositionIsInUseException(String field, String value) {
        this("Position with " + field + " : " + value + " is in use");
    }
}
