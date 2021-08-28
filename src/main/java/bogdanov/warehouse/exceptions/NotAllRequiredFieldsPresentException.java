package bogdanov.warehouse.exceptions;

public class NotAllRequiredFieldsPresentException extends RuntimeException{
    public NotAllRequiredFieldsPresentException() {
        super();
    }

    public NotAllRequiredFieldsPresentException(String message) {
        super(message);
    }

}
