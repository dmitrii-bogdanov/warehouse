package bogdanov.warehouse.exceptions;

public class NomenclatureBlankCodeException extends RuntimeException{
    public NomenclatureBlankCodeException() {
        super();
    }

    public NomenclatureBlankCodeException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
