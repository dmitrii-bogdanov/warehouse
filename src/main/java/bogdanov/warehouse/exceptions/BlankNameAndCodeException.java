package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class BlankNameAndCodeException extends RuntimeException{

    public BlankNameAndCodeException() {
        super();
    }

    public BlankNameAndCodeException(String message) {
        super(message);
    }
}
