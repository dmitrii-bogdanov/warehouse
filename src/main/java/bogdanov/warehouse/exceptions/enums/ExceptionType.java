package bogdanov.warehouse.exceptions.enums;

import lombok.Getter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@Getter
public enum ExceptionType {

    ALREADY_REGISTERED_PERSON("Person with id : #ID has already been registered as user", HttpStatus.BAD_REQUEST),
    ALREADY_RECORDED_NAME_OR_CODE("#FIELD_NAME : #FIELD_VALUE belongs to nomenclature with id : #ID", HttpStatus.BAD_REQUEST),
    NAME_OR_CODE_IS_ALREADY_REGISTERED("#FIELD_NAME : #FIELD_VALUE is already registered", HttpStatus.BAD_REQUEST),
    BLANK_CODE("Code value is missing", HttpStatus.BAD_REQUEST),
    BLANK_NAME("Name value is missing", HttpStatus.BAD_REQUEST),
    BLANK_ENTITY_NAME("#ENTITY name value is missing", HttpStatus.BAD_REQUEST),
    BLANK_NAME_AND_CODE("Name and code values are missing", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_AMOUNT("Nomenclature with id : #ID has not enough amount to write-off #FIELD_VALUE", HttpStatus.BAD_REQUEST),
    NOT_POSITIVE_AMOUNT("Amount value is negative or missing", HttpStatus.BAD_REQUEST),
    NOT_ALL_PERSON_REQUIRED_FIELDS("Person firstname, lastname and date of birth should be present", HttpStatus.BAD_REQUEST),
    NULL_ID("Id value is missing", HttpStatus.BAD_REQUEST),
    NOT_VALID_PASSWORD("Password is not valid", HttpStatus.BAD_REQUEST),
    POSITION_IS_IN_USE("Position with name : #FIELD_VALUE is in use", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_POSITION_NAME("Position name : #FIELD_VALUE is already registered", HttpStatus.BAD_REQUEST),
    ID_NAME_INCORRECT("#ENTITY id/name is incorrect", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("#ENTITY with #FIELD_NAME : #FIELD_VALUE not found", HttpStatus.NOT_FOUND),
    ALREADY_REGISTERED_USERNAME("User with username : #FIELD_VALUE already registered", HttpStatus.BAD_REQUEST),
    ID_USERNAME_INCORRECT("User id/username is incorrect", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_OR_BLANK_USERNAME("Username is already registered or blank", HttpStatus.BAD_REQUEST),
    BLANK_USERNAME("Username value is missing", HttpStatus.BAD_REQUEST),
    USER_HAS_RECORDS("User with id : #ID has records", HttpStatus.BAD_REQUEST),
    NOMENCLATURE_HAS_RECORDS("Nomenclature with id : #ID has records", HttpStatus.BAD_REQUEST),
    NOMENCLATURE_AMOUNT_IS_POSITIVE("Nomenclature with id : #ID amount is positive", HttpStatus.BAD_REQUEST),
    NO_PARAMETER_IS_PRESENT("No parameter is present", HttpStatus.BAD_REQUEST),
    LIST_CONTAINS_REPEATING_VALUES("Sent list contains repeating #FIELD_NAMEs", HttpStatus.BAD_REQUEST),
    RESERVED_VALUE("Value of #FIELD_NAME : \"#FIELD_VALUE\" is reserved", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER("Phone number contains no digit", HttpStatus.BAD_REQUEST),
    INCORRECT_RANGE("#FROM_PARAMETER_NAME is greater than #TO_PARAMETER_NAME", HttpStatus.BAD_REQUEST),
    LONG_VALUE_OVERFLOW("Operation leads to long value overflow", HttpStatus.BAD_REQUEST),
    NO_OBJECT_WAS_PASSED("No object was passed", HttpStatus.BAD_REQUEST),
    NULL_PROPERTY_WAS_PASSED("#MESSAGE", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    NUMBER_FORMAT_EXCEPTION("Incorrect number format", HttpStatus.BAD_REQUEST);

    private static final String ID = "#ID";
    private static final String FIELD_NAME = "#FIELD_NAME";
    private static final String FIELD_VALUE = "#FIELD_VALUE";
    private static final String ENTITY = "#ENTITY";
    private static final String FROM_PARAMETER_NAME = "#FROM_PARAMETER_NAME";
    private static final String TO_PARAMETER_NAME = "#TO_PARAMETER_NAME";
    private static final String MESSAGE = "#MESSAGE";

    private final String message;
    private final HttpStatus status;
    private String modifiedMessage;

    ExceptionType(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public synchronized ExceptionType setId(long id) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(ID, Long.toString(id));
        return this;
    }

    public synchronized ExceptionType setEntity(String entity) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(ENTITY, entity);
        return this;
    }

    public synchronized ExceptionType setEntity(Class entity) {
        return setEntity(entity.getSimpleName().replace("Entity", Strings.EMPTY));
    }

    public synchronized ExceptionType setFieldName(String field) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(FIELD_NAME, field);
        return this;
    }

    public synchronized ExceptionType setFieldValue(Object fieldValue) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(FIELD_VALUE, String.valueOf(fieldValue));
        return this;
    }

    public synchronized ExceptionType setFrom(String parameter) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(FROM_PARAMETER_NAME, parameter);
        return this;
    }

    public synchronized ExceptionType setTo(String parameter) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(TO_PARAMETER_NAME, parameter);
        return this;
    }

    public synchronized ExceptionType setMessage(String message) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace(MESSAGE, message);
        return this;
    }

    public synchronized ExceptionType addComment(String comment) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage += ';' + ' ' + comment;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public synchronized String getModifiedMessage() {
        if (modifiedMessage == null) {
            return message;
        } else {
            String tmp = modifiedMessage;
            modifiedMessage = null;
            return tmp;
        }
    }

    }
