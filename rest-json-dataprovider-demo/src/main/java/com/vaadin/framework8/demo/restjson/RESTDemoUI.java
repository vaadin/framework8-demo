package com.vaadin.framework8.demo.restjson;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

@Theme(ValoTheme.THEME_NAME)
public class RESTDemoUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid<JsonObject> personGrid = new Grid<>();
        personGrid.addColumn(json -> safeGetString(json, "name", "first"))
                .setCaption("First name");
        personGrid.addColumn(json -> safeGetString(json, "name", "last"))
                .setCaption("Last name");
        personGrid.addColumn(json -> safeGetString(json, "email"))
                .setCaption("Email");
        personGrid.addColumn(json -> safeGetString(json, "location", "city"))
                .setCaption("City");
        personGrid.addColumn(json -> safeGetString(json, "location", "street"))
                .setCaption("Street");
        personGrid
                .addColumn(json -> safeGetString(json, "location", "postcode"))
                .setCaption("Postal code");
        personGrid.addColumn(json -> safeGetString(json, "location", "state"))
                .setCaption("State");

        personGrid.setDataProvider(new RestDataProvider(
                "https://randomuser.me/api/1.1/?seed=0&results=50&page=1"));

        personGrid.setSizeFull();
        setContent(personGrid);
    }

    private String safeGetString(JsonObject json, String... path) {
        for (int i = 0; i < path.length - 1; i++) {
            json = json.get(path[i]);
            if (json == null) {
                return "";
            }
        }
        JsonValue value = json.get(path[path.length - 1]);
        if (value == null) {
            return "";
        }
        return value.asString();
    }

    @WebServlet(urlPatterns = "/*", name = "RESTDemoUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = RESTDemoUI.class, productionMode = false)
    public static class RESTDemoUIServlet extends VaadinServlet {
    }
}
