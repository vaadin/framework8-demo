package org.vaadin.example.treegrid.jdbc.pojo;

/**
* POJO for Person entity
 */
public class Person extends NamedItem {

    private final long departmentId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gender;

    public Person(long id,long departmentId, String firstName, String lastName, String email, String gender) {
        super(id);
        this.departmentId = departmentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    @Override
    public String getName() {
        return getFirstName() + " " + getLastName();
    }

}
