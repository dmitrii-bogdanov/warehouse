package bogdanov.warehouse.exceptions;

public class BlankNameException extends RuntimeException {
    public BlankNameException(String message) {
        super(message);
    }

    public BlankNameException() {

    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
