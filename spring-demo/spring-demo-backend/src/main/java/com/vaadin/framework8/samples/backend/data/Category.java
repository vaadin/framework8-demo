package com.vaadin.framework8.samples.backend.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
public class Category implements Serializable {

    @NotNull
    @Id
    @GeneratedValue
    private int id = -1;

    @Version
    private int version;

    @NotNull
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass().equals(Category.class)) {
            Category category = (Category) obj;
            return category.getId() == getId() && category.version == version
                    && Objects.equals(category.getName(), getName());
        }
        return false;
    }
}
