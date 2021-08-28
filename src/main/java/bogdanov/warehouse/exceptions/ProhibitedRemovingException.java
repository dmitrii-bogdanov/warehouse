package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class ProhibitedRemovingException extends RuntimeException{

    public ProhibitedRemovingException() {
        super();
    }

    public ProhibitedRemovingException(String message) {
        super(message);
    }
}
