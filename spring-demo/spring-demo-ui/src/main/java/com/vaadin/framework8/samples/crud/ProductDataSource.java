package com.vaadin.framework8.samples.crud;

import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.server.data.DataSource;

/**
 * Interface for ProductDataSource.
 */
public interface ProductDataSource extends DataSource<Product> {

    /**
     * Sets the filtering text for this DataSource.
     * <p>
     * TODO: This method should be removed once more generic filtering support
     * is implemented. Should be replaced by a filter provided through the
     * component itself.
     * 
     * @param filterText
     *          the filtering text
     */
    public void setFilterText(String filterText);

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
