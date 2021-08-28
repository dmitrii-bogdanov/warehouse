package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class PasswordException extends RuntimeException{
    public PasswordException() {
        super();
    }

    public PasswordException(String message) {
        super(message);
    }

}
