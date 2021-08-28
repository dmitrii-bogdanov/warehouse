package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class UsernameException extends RuntimeException{
    public UsernameException() {
        super();
    }

    public UsernameException(String message) {
        super(message);
    }

}
