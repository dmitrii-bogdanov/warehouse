package bogdanov.warehouse.database.entities;

enum ROLE {
    ROLE_ADMIN(1L),
    ROLE_STAFF(2L),
    ROLE_USER(3L);

    private final long id;

    ROLE(final long id) {
        this.id = id;
    }

    long getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("ROLE{id=%d, name='%s'}", id, name());
    }
}
