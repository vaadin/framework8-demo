package com.vaadin.framework8.demo.rest.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonsWrapper {

    List<Person> results;

    public List<Person> getResults() {
        return results;
    }

    public void setResults(List<Person> result) {
        results = result;
    }
}
