package org.vaadin.example.treegrid.jdbc.pojo;

/**
 * Base class for all the entities
 */
public abstract class  NamedItem {
    private final long id;

    public NamedItem(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedItem namedItem = (NamedItem) o;

        return id == namedItem.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
