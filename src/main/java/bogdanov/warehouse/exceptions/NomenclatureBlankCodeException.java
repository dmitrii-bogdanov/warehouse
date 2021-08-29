package bogdanov.warehouse.exceptions;

public class NomenclatureBlankCodeException extends RuntimeException{
    public NomenclatureBlankCodeException() {
        this("Nomenclature code is missing or blank");
    }

    public NomenclatureBlankCodeException(String message) {
        super(message);
    }

}
