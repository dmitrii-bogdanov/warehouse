package bogdanov.warehouse.exceptions;

//TODO
public class NomenclatureWrongIdNameCodeException extends RuntimeException{
    public NomenclatureWrongIdNameCodeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
