package bogdanov.warehouse.exceptions;

import org.springframework.stereotype.Component;

@Component
public class AlreadyRegisteredPersonException extends RuntimeException{

    public AlreadyRegisteredPersonException() {
        super();
    }

    public AlreadyRegisteredPersonException(String message) {
        super(message);
    }

    public AlreadyRegisteredPersonException(Long id) {
        this("Person with id : " + id + " has already been registered as user");
    }

}
