package bogdanov.warehouse.database.enums;

public enum OrderType {
    RECEPTION(1L),
    RELEASE(2L),
    INVENTORYING(3L),
    VALIDATION(4L);

    private final long id;

    OrderType(final long id) {
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
