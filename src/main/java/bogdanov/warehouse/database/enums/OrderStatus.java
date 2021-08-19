package bogdanov.warehouse.database.enums;

public enum OrderStatus {
    SUBMITTED(1L),
    ACCEPTED(2L),
    APPROVED(3L),
    COMPLETED(4L),
    CLOSED(5L),
    ABORTED(6L);

    private final long id;

    OrderStatus(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("OrderStatus{id=%d, name='%s'}", id, name());
    }
}
