package bogdanov.warehouse.exceptions;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String entity, String field, Object value) {
        this(entity + " with " + field + " : " + value.toString() + " not found");
    }
}

//#EntityName with #FieldName : #FieldValue not found