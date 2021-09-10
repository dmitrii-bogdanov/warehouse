package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.exceptions.enums.ExceptionType;

public abstract class WarehouseExeption extends RuntimeException{

    private ExceptionType exceptionType;

    protected WarehouseExeption(ExceptionType exceptionType) {
        super();
        this.exceptionType = exceptionType;
    }

    protected void setExceptionMessage(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionMessage() {
        return exceptionType;
    }
}
