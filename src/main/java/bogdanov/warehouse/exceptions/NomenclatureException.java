package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.dto.NomenclatureDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.TreeMap;

//TODO
@Data
public class NomenclatureException extends RuntimeException {

    private final String exception = getClass().toString();
    private final Map<NomenclatureDTO, String> exceptions = new TreeMap<>();

    public NomenclatureException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public void put(NomenclatureDTO n, RuntimeException e) {
        String tmp = exceptions.getOrDefault(n, Strings.EMPTY);
        exceptions.put(
                n,
                tmp + (tmp.isBlank() ? "" : "\n") + e.getClass() + " : " + e.getMessage()
        );
    }

}
