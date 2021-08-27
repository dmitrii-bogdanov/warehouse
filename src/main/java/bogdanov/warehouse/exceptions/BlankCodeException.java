package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class BlankCodeException extends RuntimeException{

    public BlankCodeException() {
        super();
    }

    public BlankCodeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
