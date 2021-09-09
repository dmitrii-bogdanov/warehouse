package bogdanov.warehouse.dto;

import bogdanov.warehouse.exceptions.enums.ExceptionMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListOfExceptionMessagesDTO {
//TODO Make lists by types
    private static final String[] EXCEPTION_MESSAGES;
    static {
        ExceptionMessage[] tmp = ExceptionMessage.values();
        EXCEPTION_MESSAGES = new String[tmp.length];
        int i = 0;
        for (ExceptionMessage m : ExceptionMessage.values()) {
            EXCEPTION_MESSAGES[i++] = m.getMessage();
        }
    }

}
