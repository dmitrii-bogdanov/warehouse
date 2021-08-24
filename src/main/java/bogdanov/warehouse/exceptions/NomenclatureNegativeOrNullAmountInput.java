package bogdanov.warehouse.exceptions;

public class NomenclatureNegativeOrNullAmountInput extends RuntimeException{

    public NomenclatureNegativeOrNullAmountInput() {
        super();
    }

    public NomenclatureNegativeOrNullAmountInput(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
