package org.vaadin.example.treegrid.jdbc.pojo;

/**
 * POJO for Company entity
 */
public class Company extends NamedItem {
    private final String name;
    private final String email;

    public Company(long id,String name, String email) {
        super(id);
        this.name = name;
        this.email = email;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
