package bogdanov.warehouse.exceptions;

import bogdanov.warehouse.dto.NomenclatureDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

@Data
@JsonPropertyOrder({"exception", "accepted", "exceptions"})
public class NomenclatureException extends RuntimeException {

    private final List<NomenclatureDTO> accepted = new LinkedList<>();
    private final String exception = getClass().getSimpleName();
    private final Map<String, String> exceptions = new TreeMap<>();

    public NomenclatureException(String message) {
        super(message);
    }

    public NomenclatureException() {
        super();
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
            this.add(entry.getKey(), entry.getValue());
        }
        boolean thisContainsAccepted;
        boolean thisContainsException;
        if (!e.accepted.isEmpty()) {
            for (NomenclatureDTO dto : e.accepted) {
                thisContainsAccepted = this.accepted.contains(dto);
                thisContainsException = this.exceptions.containsKey(dto.toFormattedString());
                if (!(thisContainsAccepted || thisContainsException)) {
                    this.accept(dto);
                }
            }
        }
        this.accepted.removeIf(dto -> this.exceptions.containsKey(dto.toFormattedString()));
    }

    @JsonIgnore
    public boolean isEmpty() {
        return exceptions.isEmpty();
    }

    @JsonIgnore
    public boolean isNotEmpty() {
        return !exceptions.isEmpty();
    }

    public void accept(NomenclatureDTO dto) {
        accepted.add(dto);
    }

    public void acceptAll(Collection<NomenclatureDTO> dto) {
        accepted.addAll(dto);
    }

    public int countAccepted() {
        return accepted.size();
    }

    public int size() {
        return exceptions.size();
    }

    @JsonIgnore
    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @JsonIgnore
    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }

    @JsonIgnore
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @JsonIgnore
    @Override
    public StackTraceElement[] getStackTrace() {
        return super.getStackTrace();
    }
}
