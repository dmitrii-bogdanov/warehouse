package bogdanov.warehouse.exceptions;

//TODO
public class NomenclatureAlreadyTakenNameException extends RuntimeException{
    public NomenclatureAlreadyTakenNameException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
