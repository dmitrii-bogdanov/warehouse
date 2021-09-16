package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.exceptions.enums.ExceptionType;

public class IntervalServerException extends WarehouseExeption{

    public IntervalServerException(ExceptionType exceptionType) {
        super(exceptionType);
    }

}
