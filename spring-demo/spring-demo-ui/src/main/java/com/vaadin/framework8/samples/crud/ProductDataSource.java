package com.vaadin.framework8.samples.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.backend.repository.CategoryRepository;
import com.vaadin.framework8.samples.backend.repository.ProductRepository;
import com.vaadin.server.data.AbstractDataSource;
import com.vaadin.server.data.Query;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;

/**
 * DataSource implementation for managing {@code ProductRepository} and
 * filtering.
 * <p>
 * <strong>Note:</strong> This implementation can't be used between multiple
 * different components as the filtering is actually stateful and not stateless.
 */
@Component
// TODO: Use common data source for all UIs after backend filtering is done
@UIScope
public class ProductDataSource extends AbstractDataSource<Product> {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    private String filterText;

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(Query t) {
        return (int) getItems().count();
    }

    @Override
    public Stream<Product> apply(Query t) {
        // TODO: Transform Query into Pageable
        return getItems();
    }

    /**
     * Sets the filtering text for this DataSource.
     * 
     * @param filterText
     */
    public void setFilterText(String filterText) {
        if (Objects.equals(this.filterText, filterText)) {
            return;
        }
        this.filterText = filterText;
        refreshAll();
    }

    /**
     * Store given product to the repository.
     * 
     * @param product
     *            the updated or new product
     */
    public void save(Product product) {
        productRepo.save(product);
        refreshAll();
    }

    /**
     * Delete given product from the repository.
     * 
     * @param product
     *            the product to be deleted
     */
    public void delete(Product product) {
        productRepo.delete(product);
        refreshAll();
    }

    private Collection<Category> getFilteredCategories(String string) {
        return categoryRepo.findAllByNameContainingIgnoreCase(string);
    }

    private List<Availability> getFilteredAvailabilities(String string) {
        Locale locale = UI.getCurrent().getLocale();
        return Arrays.stream(Availability.values()).filter(a -> {
            return a.name().toLowerCase(locale).contains(string);
        }).collect(Collectors.toList());
    }

    private Stream<Product> getItems() {
        if (filterText == null || filterText.isEmpty()) {
            return productRepo.findAll().stream();
        }
        String filter = filterText.toLowerCase(UI.getCurrent().getLocale());
        return productRepo
                .findAllByProductNameContainingIgnoreCaseOrAvailabilityInOrCategoryIn(
                        filter, getFilteredAvailabilities(filter),
                        getFilteredCategories(filter))
                .stream();
    }
}