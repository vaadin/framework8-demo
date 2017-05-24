package org.vaadin.example.treegrid.jdbc.pojo;

/**
 * POJO for Company entity
 */
public class Company extends NamedItem {
    private final String name;

    public Company(long id,String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
