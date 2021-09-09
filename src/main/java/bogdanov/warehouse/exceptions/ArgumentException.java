package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import lombok.Getter;

@Getter
public class ArgumentException extends RuntimeException{

    private ExceptionMessage exceptionMessage;

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException() {
        super();
    }

    public ArgumentException(ExceptionMessage exceptionMessage) {
        this(exceptionMessage.getModifiedMessage());
        this.exceptionMessage = exceptionMessage;
    }
}
