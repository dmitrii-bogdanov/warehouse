package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class AlreadyRecordedNameOrCodeException extends RuntimeException{

    public AlreadyRecordedNameOrCodeException() {
        super();
    }

    public AlreadyRecordedNameOrCodeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
