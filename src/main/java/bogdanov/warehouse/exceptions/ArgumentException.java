package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.exceptions.enums.ExceptionType;

public class ArgumentException extends WarehouseExeption{

    public ArgumentException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
