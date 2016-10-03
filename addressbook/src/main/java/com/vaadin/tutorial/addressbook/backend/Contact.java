package com.vaadin.tutorial.addressbook.backend;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

import org.apache.commons.beanutils.BeanUtils;

/**
 * A simple DTO for the address book example.
 *
 * Serializable and cloneable Java Object that are typically persisted in the
 * database and can also be easily converted to different formats like JSON.
 */
// Backend DTO class. This is just a typical Java backend implementation
// class and nothing Vaadin specific.
public class Contact implements Serializable, Cloneable {

    private Long id;

    private String firstName = "";
    private String lastName = "";
    private String phone = "";
    private String email = "";
    private LocalDate birthDate;
    private boolean doNotCall;
    private Instant createdTimestamp;

    public Contact() {
        createdTimestamp = Instant.now();
    }

    public long getCreatedTimestamp() {
        return createdTimestamp.toEpochMilli();
    }

    public boolean isDoNotCall() {
        return doNotCall;
    }

    public void setDoNotCall(boolean doNotCall) {
        this.doNotCall = doNotCall;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public Contact clone() throws CloneNotSupportedException {
        try {
            Contact contact = (Contact) BeanUtils.cloneBean(this);
            contact.createdTimestamp = createdTimestamp;
            return contact;
        } catch (Exception ex) {
            throw new CloneNotSupportedException();
        }
    }

    @Override
    public String toString() {
        return "Contact{" + "id=" + id + ", firstName=" + firstName
                + ", lastName=" + lastName + ", phone=" + phone + ", email="
                + email + ", birthDate=" + birthDate + ", createdTimestamp="
                + createdTimestamp + ",doNotCall=" + doNotCall + '}';
    }

}
