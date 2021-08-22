package bogdanov.warehouse.exceptions;

//TODO
public class NomenclatureWrongIdNamePairException extends RuntimeException{
    public NomenclatureWrongIdNamePairException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
