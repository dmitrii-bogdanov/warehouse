package bogdanov.warehouse.exceptions;

//TODO
public class NomenclatureWrongIdCodePairException extends RuntimeException{
    public NomenclatureWrongIdCodePairException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}