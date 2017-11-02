package org.vaadin.example.treegrid.jdbc.pojo;

/**
 * POJO for Department entity
 */
public class Department extends NamedItem {
    private final String name;
    private final long companyId;

    public Department(long id, long companyId, String name) {
        super(id);
        this.name = name;
        this.companyId = companyId;
    }

    private long getCompanyId() {
        return companyId;
    }

    @Override
    public String getName() {
        return name;
    }

}
