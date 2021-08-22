package bogdanov.warehouse.exceptions;

public class NomenclatureNotPositiveAmount extends RuntimeException{

    public NomenclatureNotPositiveAmount() {
        super();
    }

    public NomenclatureNotPositiveAmount(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
