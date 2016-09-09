package com.vaadin.framework8.samples.backend;

import java.util.Collection;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.backend.repository.CategoryRepository;
import com.vaadin.framework8.samples.backend.repository.ProductRepository;

/**
 * Simple unit test for the back-end data service.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DataJpaTest
public class DataServiceTest {

    @Autowired
    private DataService service;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void update_product() {
        long count = productRepository.count();
        Product save = insertProduct("foo");

        Assert.assertNotEquals(-1, save.getId());

        Assert.assertEquals(count + 1, productRepository.count());
        Assert.assertEquals("foo", save.getProductName());

        save.setProductName("bar");

        Product update = service.updateProduct(save);
        Assert.assertEquals("bar", update.getProductName());
    }

    @Test
    public void dataServiceCanFetchProducts() {
        Product created = insertProduct("foo");

        Collection<Product> allProducts = service.getAllProducts();
        Optional<Product> found = allProducts.stream()
                .filter(product -> product.getId() == created.getId())
                .findFirst();
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(created.getProductName(),
                found.get().getProductName());
    }

    @Test
    public void dataServiceCanFetchCategories() {
        Category category = new Category();
        category.setName("foo");

        long count = categoryRepository.count();
        Category created = categoryRepository.save(category);
        Assert.assertNotEquals(-1, created.getId());

        Assert.assertEquals(count + 1, categoryRepository.count());

        Collection<Category> allProducts = service.getAllCategories();
        Optional<Category> found = allProducts.stream()
                .filter(cat -> cat.getId() == created.getId()).findFirst();
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(created.getName(), found.get().getName());
    }

    @Test
    public void deleteProduct() {
        Product created = insertProduct("foo");
        long count = productRepository.count();

        service.deleteProduct(created.getId());
        Assert.assertEquals(count - 1, productRepository.count());

        Assert.assertNull(productRepository.findOne(created.getId()));
    }

    @Test
    public void getProduct() {
        Product created = insertProduct("foo");

        Product product = service.getProduct(created.getId());
        Assert.assertEquals(created.getId(), product.getId());
        Assert.assertEquals(created.getProductName(), product.getProductName());
    }

    private Product insertProduct(String name) {
        Product product = new Product();
        product.setProductName(name);

        return productRepository.save(product);
    }
}
