package bogdanov.warehouse.exceptions;

public enum ExceptionMessage {

    ALREADY_REGISTERED_PERSON("Person with id : #ID has already been registered as user"),
    ALREADY_RECORDED_NAME_OR_CODE("#FIELD_NAME : #FIELD_VALUE belongs to nomenclature with id : #ID"),
    BLANK_CODE("Code value is missing or blank"),
    BLANK_NAME("Name value is missing or blank"),
    NOT_ENOUGH_AMOUNT("Nomenclature with id : #ID has not enough amount to write-off #FIELD_VALUE"),
    NOT_POSITIVE_AMOUNT("Amount value is negative or missing"),
    NOT_ALL_PERSON_REQUIRED_FIELDS("Person firstname, lastname and date of birth should be present"),
    NULL_ID("Id value is missing"),
    NOT_VALID_PASSWORD("Password is not valid"),
    POSITION_IS_IN_USE("Position with name : #FIELD_VALUE is in use"),
    RESOURCE_NOT_FOUND("#ENTITY with #FIELD_NAME : #FIELD_VALUE not found"),
    ALREADY_REGISTERED_USERNAME("User with username : #FIELD_VALUE already registered"),
    BLANK_USERNAME("Username value is blank or missing");



    private String message;
    private String modifiedMessage;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public ExceptionMessage setId(long id) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace("#ID", Long.toString(id));
        return this;
    }

    public ExceptionMessage setEntity(Class entity) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace("#ENTITY", entity.getSimpleName().replace("Entity", ""));
        return this;
    }

    public ExceptionMessage setFieldName(String field) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace("#FIELD_NAME", field);
        return this;
    }

    public ExceptionMessage setFieldValue(Object fieldValue) {
        if (modifiedMessage == null) {
            modifiedMessage = message;
        }
        modifiedMessage = modifiedMessage.replace("#FIELD_VALUE", fieldValue.toString());
        return this;
    }

    public String getMessage() {
        return message;
    }

    public String getModifiedMessage() {
        if (modifiedMessage == null) {
            return message;
        } else {
            String tmp = modifiedMessage;
            modifiedMessage = null;
            return tmp;
        }
    }

    }
