package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.database.entities.NomenclatureEntity;
import org.apache.logging.log4j.util.Strings;

public class BlankNameException extends RuntimeException {
    public BlankNameException(String message) {
        super(message);
    }

    public BlankNameException() {
        this("Name value is missing or blank");
    }

    public BlankNameException(Class entity) {
        this(entity.getSimpleName().replace("Entity", Strings.EMPTY)
                + " name value is missing or blank");
    }

}
