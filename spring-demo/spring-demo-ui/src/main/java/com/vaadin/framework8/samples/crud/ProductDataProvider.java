package com.vaadin.framework8.samples.crud;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.framework8.samples.backend.data.Product;

/**
 * Interface for ProductDataProvider.
 */
public interface ProductDataProvider extends DataProvider<Product, String> {

    /**
     * Store given product to the repository.
     *
     * @param product
     *            the updated or new product
     */
    public void save(Product product);

    /**
     * Delete given product from the repository.
     *
     * @param product
     *            the product to be deleted
     */
    public void delete(Product product);
}
