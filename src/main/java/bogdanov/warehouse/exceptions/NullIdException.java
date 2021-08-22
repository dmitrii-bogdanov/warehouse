package bogdanov.warehouse.exceptions;

//TODO
public class NullIdException extends RuntimeException{
    public NullIdException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
