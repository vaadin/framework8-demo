package com.vaadin.framework8.demo.rest;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.framework8.demo.rest.backend.PersonService;
import com.vaadin.framework8.demo.rest.model.Person;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.data.BackEndDataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class RESTDemoUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid<Person> personGrid = new Grid<>();
        personGrid.addColumn("First name", Person::getFirstName);
        personGrid.addColumn("Last name", Person::getLastName);
        personGrid.addColumn("Email", Person::getEmail);
        personGrid.addColumn("City", Person::getCity);
        personGrid.addColumn("Street", Person::getStreet);
        personGrid.addColumn("Postal code", Person::getPostCode);
        personGrid.addColumn("State", Person::getState);

        personGrid.setDataProvider(new BackEndDataProvider<Person, Object>(
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
