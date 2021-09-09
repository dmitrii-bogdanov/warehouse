package bogdanov.warehouse;

import bogdanov.warehouse.exceptions.enums.ExceptionMessage;

public class Temp {

    public static void main(String[] args) {
        System.out.println(ExceptionMessage.ALREADY_REGISTERED_PERSON.getMessage());
        System.out.println(ExceptionMessage.ALREADY_REGISTERED_PERSON.setId(23).getModifiedMessage());
        System.out.println(ExceptionMessage.ALREADY_REGISTERED_PERSON.getModifiedMessage());
        System.out.println(ExceptionMessage.ALREADY_REGISTERED_PERSON.getMessage());
    }
}
