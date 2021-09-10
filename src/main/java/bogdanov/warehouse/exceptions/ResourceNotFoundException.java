package bogdanov.warehouse.exceptions;


import bogdanov.warehouse.exceptions.enums.ExceptionType;

public class ResourceNotFoundException extends WarehouseExeption {

    public ResourceNotFoundException(String entity, String field, Object value) {
        super(ExceptionType.RESOURCE_NOT_FOUND.setEntity(entity).setFieldName(field).setFieldValue(value));
    }

    public ResourceNotFoundException(ExceptionType exceptionType) {
        super(exceptionType);
    }

}
