package bogdanov.warehouse.exceptions;


import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {

    private ExceptionMessage exceptionMessage;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String entity, String field, Object value) {
        this(ExceptionMessage.RESOURCE_NOT_FOUND
                .setEntity(entity).setFieldName(field).setFieldValue(value).getModifiedMessage());
//        this(entity + " with " + field + " : " + value.toString() + " not found");
    }

    public ResourceNotFoundException(ExceptionMessage exceptionMessage) {
        this(exceptionMessage.getModifiedMessage());
    }
}

//#EntityName with #FieldName : #FieldValue not found