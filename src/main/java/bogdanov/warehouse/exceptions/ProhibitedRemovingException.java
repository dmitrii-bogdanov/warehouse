package bogdanov.warehouse.exceptions;


import bogdanov.warehouse.exceptions.enums.ExceptionType;

public class ProhibitedRemovingException extends WarehouseExeption {

    public ProhibitedRemovingException(ExceptionType exceptionType) {
        super(exceptionType);
    }

}
