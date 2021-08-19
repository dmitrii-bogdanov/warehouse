package bogdanov.warehouse.database.enums;

public enum OperationType {
    RECEPTION(1L),
    RELEASE(2L),
    ASSEMBLY(3L),
    PACKING(4L),
    INVENTORYING(5L),
    VALIDATION(6L);


    private final long id;

    OperationType(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("OrderType{id=%d, name='%s'}", id, name());
    }
}
