package bogdanov.warehouse.database.enums;

public enum RecordType {
    RECEPTION(1L),
    RELEASE(2L),
    DELETED(3L);


    private final long id;

    RecordType(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("RecordType{id=%d, name='%s'}", id, name());
    }
}
