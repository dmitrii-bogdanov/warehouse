package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class BlankNameAndCodeException extends RuntimeException{

    public BlankNameAndCodeException() {
        super("Name and code values are missing");
    }

    public BlankNameAndCodeException(String message) {
        super(message);
    }
}
