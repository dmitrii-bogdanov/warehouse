package bogdanov.warehouse.exceptions;

public class NomenclatureNotEnoughNumberAvailable extends RuntimeException {

    public NomenclatureNotEnoughNumberAvailable() {
        super();
    }

    public NomenclatureNotEnoughNumberAvailable(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}