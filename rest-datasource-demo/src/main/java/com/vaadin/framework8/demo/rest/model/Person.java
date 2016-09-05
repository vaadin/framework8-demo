package com.vaadin.framework8.demo.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {

    private Name name;
    private Address location;
    private String email;

    public void setName(Name name) {
        this.name = name;
    }

    public void setLocation(Address location) {
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return name.getFirst();
    }

    public String getLastName() {
        return name.getLast();
    }

    public String getStreet() {
        return location.getStreet();
    }

    public String getPostCode() {
        return location.getPostcode();
    }

    public String getCity() {
        return location.getCity();
    }

    public String getState() {
        return location.getState();
    }

    public String getEmail() {
        return email;
    }
}
