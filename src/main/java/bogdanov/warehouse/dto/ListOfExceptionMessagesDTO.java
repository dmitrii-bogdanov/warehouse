package bogdanov.warehouse.dto;

import bogdanov.warehouse.exceptions.enums.ExceptionType;
import lombok.Data;

@Data
public class ListOfExceptionMessagesDTO {
//TODO Make lists by types
    private static final String[] EXCEPTION_MESSAGES;
    static {
        ExceptionType[] tmp = ExceptionType.values();
        EXCEPTION_MESSAGES = new String[tmp.length];
        int i = 0;
        for (ExceptionType m : ExceptionType.values()) {
            EXCEPTION_MESSAGES[i++] = m.getMessage();
        }
    }

}
