package bogdanov.warehouse.exceptions;

//TODO
public class NomenclatureAlreadyTakenCodeException extends RuntimeException{
    public NomenclatureAlreadyTakenCodeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}