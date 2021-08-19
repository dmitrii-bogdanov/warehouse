package bogdanov.warehouse.database.enums;

public enum OperationStatus {
    SUBMITTED(1L),
    ACCEPTED(2L),
    COMPLETED(3L),
    APPROVED(4L),
    ABORTED(5L);

    private final long id;

    OperationStatus(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("OperationStatus{id=%d, name='%s'}", id, name());
    }
}
