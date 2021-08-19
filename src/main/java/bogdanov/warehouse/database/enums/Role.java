package bogdanov.warehouse.database.enums;

public enum Role {
    ROLE_ADMIN(1L),
    ROLE_STAFF(2L),
    ROLE_USER(3L);

    private final long id;

    Role(final long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Role{id=%d, name='%s'}", id, name());
    }
}
