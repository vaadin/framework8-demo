package com.vaadin.framework8.demo.rest;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.framework8.demo.rest.backend.PersonService;
import com.vaadin.framework8.demo.rest.model.Person;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@Theme(ValoTheme.THEME_NAME)
public class RESTDemoUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid<Person> personGrid = new Grid<>();
        personGrid.addColumn(Person::getFirstName).setCaption("First name");
        personGrid.addColumn(Person::getLastName).setCaption("Last Name");
        personGrid.addColumn(Person::getEmail).setCaption("Email");
        personGrid.addColumn(Person::getCity).setCaption("City");
        personGrid.addColumn(Person::getStreet).setCaption("Street");
        personGrid.addColumn(Person::getPostCode).setCaption("Postal code");
        personGrid.addColumn(Person::getState).setCaption("State");

        personGrid
                .setDataProvider(new CallbackDataProvider<Person, Void>(
                        query -> PersonService.getInstance().fetchPeople(
                                query.getOffset(), query.getLimit()),
                        query -> PersonService.getPersonCount()));

        personGrid.setSizeFull();
        setContent(personGrid);
    }

    @WebServlet(urlPatterns = "/*", name = "RESTDemoUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = RESTDemoUI.class, productionMode = false)
    public static class RESTDemoUIServlet extends VaadinServlet {
    }
}
