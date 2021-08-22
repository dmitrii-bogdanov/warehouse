package bogdanov.warehouse.exceptions;

public class NomenclatureBlankNameException extends RuntimeException {
    public NomenclatureBlankNameException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
