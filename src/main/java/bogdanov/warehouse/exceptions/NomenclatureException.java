package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.dto.NomenclatureDTO;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

//TODO
@Data
public class NomenclatureException extends RuntimeException {

    private final Collection<NomenclatureDTO> accepted = new LinkedList<>();
    private final String exception = getClass().getSimpleName();
    private final Map<String, String> exceptions = new TreeMap<>();

    public NomenclatureException(String message) {
        super(message);
    }

    public NomenclatureException() {

    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    private void add(String nomenclature, String message) {
        String tmp = exceptions.getOrDefault(nomenclature, Strings.EMPTY);
        exceptions.put(
                nomenclature,
                tmp + (tmp.isBlank() ? "" : "\n") + message
        );
    }

    public void add(NomenclatureDTO n, RuntimeException e) {
        add(n.toFormattedString(), e.getClass().getSimpleName() + " : " + e.getMessage());
    }

    public void add(NomenclatureException e) {
        for (Map.Entry<String, String> entry : e.exceptions.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
    }

    public boolean isEmpty() {
        return exceptions.isEmpty();
    }

    public boolean isNotEmpty() {
        return !exceptions.isEmpty();
    }

    public void accept(NomenclatureDTO dto) {
        accepted.add(dto);
    }

    public void acceptAll(Collection<NomenclatureDTO> dto) {
        accepted.addAll(dto);
    }

}
