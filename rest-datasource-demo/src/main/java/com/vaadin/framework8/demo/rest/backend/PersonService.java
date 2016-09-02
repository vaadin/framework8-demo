package com.vaadin.framework8.demo.rest.backend;

import java.util.stream.Stream;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.vaadin.framework8.demo.rest.model.Person;
import com.vaadin.framework8.demo.rest.model.PersonsWrapper;

public class PersonService {

    private final static PersonService instance = new PersonService();

    private final static WebTarget resource = ClientBuilder.newBuilder()
            .register(JacksonFeature.class).build()
            .target("http://api.randomuser.me/");

    public static PersonService getInstance() {
        return instance;
    }

    public Stream<Person> fetchPeople(int offset, int num) {
        PersonsWrapper res = resource.queryParam("seed", 0)
                .queryParam("results", 200).queryParam("page", 1)
                .request(MediaType.APPLICATION_JSON).get(PersonsWrapper.class);
        return res.getResults().stream();
    }

    public static int getPersonCount() {
        return 200;
    }
}
