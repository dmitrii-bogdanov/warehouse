package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.exceptions.enums.ExceptionType;

public abstract class WarehouseExeption extends RuntimeException{

    private ExceptionType exceptionType;

    protected WarehouseExeption(ExceptionType exceptionType) {
        super();
        setExceptionType(exceptionType);
    }

    protected void setExceptionType(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

}
