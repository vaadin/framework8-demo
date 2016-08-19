package com.vaadin.framework8.samples.backend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.backend.repository.CategoryRepository;
import com.vaadin.framework8.samples.backend.repository.ProductRepository;

/**
 * Mock data initializer.
 * <p>
 * Fills in-memory data base each time when application is started.
 * 
 * @author Vaadin Ltd
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MockDataGenerator {

    @Autowired
    private ProductRepository productRespoitory;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final Random random = new Random(1);
    private static final String categoryNames[] = new String[] {
            "Children's books", "Best sellers", "Romance", "Mystery",
            "Thriller", "Sci-fi", "Non-fiction", "Cookbooks" };

    private static String[] word1 = new String[] { "The art of", "Mastering",
            "The secrets of", "Avoiding", "For fun and profit: ",
            "How to fail at", "10 important facts about",
            "The ultimate guide to", "Book of", "Surviving", "Encyclopedia of",
            "Very much", "Learning the basics of", "The cheap way to",
            "Being awesome at", "The life changer:", "The Vaadin way:",
            "Becoming one with", "Beginners guide to",
            "The complete visual guide to", "The mother of all references:" };

    private static String[] word2 = new String[] { "gardening",
            "living a healthy life", "designing tree houses", "home security",
            "intergalaxy travel", "meditation", "ice hockey",
            "children's education", "computer programming", "Vaadin TreeTable",
            "winter bathing", "playing the cello", "dummies", "rubber bands",
            "feeling down", "debugging", "running barefoot",
            "speaking to a big audience", "creating software", "giant needles",
            "elephants", "keeping your wife happy" };

    @PostConstruct
    private void init() {
        List<Category> categories = createCategories();
        createProducts(categories);
    }

    private List<Category> createCategories() {
        List<Category> categories = new ArrayList<>();
        for (String name : categoryNames) {
            Category c = createCategory(name);
            categories.add(c);
        }
        return categories;

    }

    private void createProducts(List<Category> categories) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Product p = createProduct(categories);
            products.add(p);
        }
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    private Product createProduct(List<Category> categories) {
        Product product = new Product();
        product.setProductName(generateName());

        product.setPrice(new BigDecimal((random.nextInt(250) + 50) / 10.0));
        product.setAvailability(Availability.values()[random
                .nextInt(Availability.values().length)]);
        if (product.getAvailability() == Availability.AVAILABLE) {
            product.setStockCount(random.nextInt(523));
        }

        product.setCategory(getCategory(categories, 1, 2));
        return productRespoitory.save(product);
    }

    private Set<Category> getCategory(List<Category> categories, int min,
            int max) {
        int nr = random.nextInt(max) + min;
        HashSet<Category> productCategories = new HashSet<>();
        for (int i = 0; i < nr; i++) {
            productCategories
                    .add(categories.get(random.nextInt(categories.size())));
        }

        return productCategories;
    }

    private String generateName() {
        return word1[random.nextInt(word1.length)] + " "
                + word2[random.nextInt(word2.length)];
    }

}
