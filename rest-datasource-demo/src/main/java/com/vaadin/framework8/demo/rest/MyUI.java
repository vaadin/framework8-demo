package com.vaadin.framework8.demo.rest;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.tokka.ui.components.grid.Grid;
import com.vaadin.ui.UI;

@Theme("mytheme")
@Widgetset("com.vaadin.test.test.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Grid<GOTCharacter> characterGrid = new Grid<GOTCharacter>();
        characterGrid.addColumn("name", GOTCharacter::getName);
        characterGrid.addColumn("culture", GOTCharacter::getCulture);
        characterGrid.addColumn("gender", GOTCharacter::getGender);
        characterGrid.addColumn("born", GOTCharacter::getBorn);
        characterGrid.addColumn("died", GOTCharacter::getDied);
        characterGrid.setDataSource(new GOTDataSource());
        characterGrid.setSizeFull();
        setContent(characterGrid);
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
