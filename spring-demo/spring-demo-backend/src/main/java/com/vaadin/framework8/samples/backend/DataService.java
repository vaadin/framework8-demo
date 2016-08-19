package com.vaadin.framework8.samples.backend;

import java.io.Serializable;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.backend.repository.CategoryRepository;
import com.vaadin.framework8.samples.backend.repository.ProductRepository;

/**
 * Back-end service interface for retrieving and updating product data.
 */
@Service
@Transactional
public class DataService implements Serializable {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Collection<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Collection<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(int productId) {
        productRepository.delete(productId);
    }

    public Product getProduct(int productId) {
        return productRepository.findOne(productId);
    }

}
