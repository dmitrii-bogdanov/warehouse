package bogdanov.warehouse.exceptions;

public class NotAllRequiredFieldsPresentException extends RuntimeException{
    public NotAllRequiredFieldsPresentException() {
        super();
    }

    public NotAllRequiredFieldsPresentException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
