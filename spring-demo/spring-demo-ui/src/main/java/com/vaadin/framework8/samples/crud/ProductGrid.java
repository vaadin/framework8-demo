package com.vaadin.framework8.samples.crud;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

/**
 * Grid of products, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data provider that is suitable for
 * small data sets.
 */
public class ProductGrid extends Grid<Product> {

    public ProductGrid() {
        setSizeFull();

        addColumn(p -> String.valueOf(p.getId())).setCaption("Id")
                .setSortProperty("id");
        addColumn(Product::getProductName).setCaption("Product Name")
                .setSortProperty("productName");
        addColumn(Product::getPrice, new NumberRenderer()).setCaption("Price")
                .setStyleGenerator(c -> "align-right").setSortProperty("price");
        addColumn(p -> {
            Availability availability = p.getAvailability();
            return getTrafficLightIconHtml(availability) + " "
                    + availability.name();
        }, new HtmlRenderer()).setCaption("Availability")
                .setSortProperty("availability");
        addColumn(Product::getStockCount, new NumberRenderer())
                .setCaption("Stock Count").setStyleGenerator(c -> "align-right")
                .setSortProperty("stockCount");
        addColumn(p -> p.getCategory().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(Category::getName).collect(Collectors.joining(", ")))
                        .setCaption("Category").setSortable(false);
    }

    private String getTrafficLightIconHtml(Availability availability) {
        String color = "";
        if (availability == Availability.AVAILABLE) {
            color = "#2dd085";
        } else if (availability == Availability.COMING) {
            color = "#ffc66e";
        } else if (availability == Availability.DISCONTINUED) {
            color = "#f54993";
        }

        String iconCode = "<span class=\"v-icon\" style=\"font-family: "
                + VaadinIcons.CIRCLE.getFontFamily() + ";color:" + color
                + "\">&#x"
                + Integer.toHexString(VaadinIcons.CIRCLE.getCodepoint())
                + ";</span>";
        return iconCode;
    }

    public Product getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Product product) {
        getDataCommunicator().refresh(product);
    }
}
