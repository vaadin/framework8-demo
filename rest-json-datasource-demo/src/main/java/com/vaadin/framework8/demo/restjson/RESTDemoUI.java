package com.vaadin.framework8.demo.restjson;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.io.IOUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.data.BackEndDataSource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;

@Theme(ValoTheme.THEME_NAME)
public class RESTDemoUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid<JsonObject> personGrid = new Grid<>();
        personGrid.addColumn("First name",
                json -> json.getObject("name").getString("first"));
        personGrid.addColumn("Last name",
                json -> json.getObject("name").getString("last"));
        personGrid.addColumn("Email", json -> json.getString("email"));
        personGrid.addColumn("City",
                json -> json.getObject("location").getString("city"));
        personGrid.addColumn("Street",
                json -> json.getObject("location").getString("street"));
        personGrid.addColumn("Postal code",
                json -> json.getObject("location").get("postcode").asString());
        personGrid.addColumn("State",
                json -> json.getObject("location").getString("state"));

        personGrid.setDataSource(new BackEndDataSource<JsonObject>(query -> {
            URL url;
            try {
                url = new URL(
                        "http://api.randomuser.me/?seed=0&results=50&page=1");
                String jsonData = IOUtils.toString(url, StandardCharsets.UTF_8);
                JsonObject json = Json.parse(jsonData);
                JsonArray results = json.getArray("results");

                return jsonArrayStream(results);
            } catch (IOException e) {
                e.printStackTrace();
                List<JsonObject> empty = Collections.emptyList();
                return empty.stream();
            }
        }, query -> 200));

        personGrid.setSizeFull();
        setContent(personGrid);
    }

    private static Stream<JsonObject> jsonArrayStream(JsonArray array) {
        List<JsonObject> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getObject(i));
        }
        return list.stream();
    }

    @WebServlet(urlPatterns = "/*", name = "RESTDemoUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = RESTDemoUI.class, productionMode = false)
    public static class RESTDemoUIServlet extends VaadinServlet {
    }
}
