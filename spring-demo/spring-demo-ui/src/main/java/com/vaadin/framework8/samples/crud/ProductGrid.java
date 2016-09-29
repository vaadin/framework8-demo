package com.vaadin.framework8.samples.crud;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;

/**
 * Grid of products, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
public class ProductGrid extends Grid<Product> {

    public ProductGrid() {
        setSizeFull();

        // TODO: Add sorting for backend
        addColumn("Id", p -> String.valueOf(p.getId())).setSortProperty("id");
        addColumn("Product Name", Product::getProductName)
                .setSortProperty("productName");
        addColumn("Price", Product::getPrice, new NumberRenderer())
                .setStyleGenerator(c -> "align-right").setSortProperty("price");
        addColumn("Availability", p -> {
            Availability availability = p.getAvailability();
            return getTrafficLightIconHtml(availability) + " "
                    + availability.name();
        }, new HtmlRenderer()).setSortProperty("availability");
        addColumn("Stock Count", Product::getStockCount, new NumberRenderer())
                .setStyleGenerator(c -> "align-right")
                .setSortProperty("stockCount");
        addColumn("Category", p -> p.getCategory().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(Category::getName).collect(Collectors.joining(", ")))
                        .setSortable(false);
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
                + FontAwesome.CIRCLE.getFontFamily() + ";color:" + color
                + "\">&#x"
                + Integer.toHexString(FontAwesome.CIRCLE.getCodepoint())
                + ";</span>";
        return iconCode;
    }

    public Product getSelectedRow() {
        return getSelectedItem().orElse(null);
    }

    public void refresh(Product product) {
        getDataCommunicator().refresh(product);
    }
}
