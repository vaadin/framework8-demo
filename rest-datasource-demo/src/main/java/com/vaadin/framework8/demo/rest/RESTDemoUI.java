package com.vaadin.framework8.demo.rest;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.framework8.demo.rest.backend.PersonService;
import com.vaadin.framework8.demo.rest.model.Person;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.data.BackEndDataSource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

@Theme("mytheme")
public class RESTDemoUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid<Person> personGrid = new Grid<>();
        personGrid.addColumn("First name", String.class, Person::getFirstName);
        personGrid.addColumn("Last name", String.class, Person::getLastName);
        personGrid.addColumn("Email", String.class, Person::getEmail);
        personGrid.addColumn("City", String.class, Person::getCity);
        personGrid.addColumn("Street", String.class, Person::getStreet);
        personGrid.addColumn("Postal code", String.class, Person::getPostCode);
        personGrid.addColumn("State", String.class, Person::getState);

        personGrid.setDataSource(new BackEndDataSource<Person>(
                query -> PersonService.getInstance()
                        .fetchPeople(query.getOffset(), query.getLimit()),
                query -> PersonService.getPersonCount()));

        personGrid.setSizeFull();
        setContent(personGrid);
    }

    @WebServlet(urlPatterns = "/*", name = "RESTDemoUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = RESTDemoUI.class, productionMode = false)
    public static class RESTDemoUIServlet extends VaadinServlet {
    }
}
