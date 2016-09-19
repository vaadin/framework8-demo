/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.framework8.samples;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vaadin.demo.testutil.AbstractDemoTest;
import com.vaadin.framework8.samples.backend.DataService;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.testbench.elements.PasswordFieldElement;

/**
 * @author Vaadin Ltd
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfig.class)
public class SpringCrudIT extends AbstractDemoTest {

    @Autowired
    private DataService dataService;

    @Before
    public void setUp() {
        open();
    }

    @Test
    public void login() {
        doLogin();

        Assert.assertTrue(isElementPresent(By.className("v-grid")));
    }

    @Test
    public void tableWithData() {
        doLogin();
        WebElement table = findElement(By.className("v-grid"));

        List<WebElement> headers = table.findElements(By.tagName("th"));
        Assert.assertEquals(6, headers.size());

        hasText(headers.get(0), "Id");
        hasText(headers.get(1), "Product Name");
        hasText(headers.get(2), "Price");
        hasText(headers.get(3), "Availability");
        hasText(headers.get(4), "Stock Count");
        hasText(headers.get(5), "Category");

        List<WebElement> rows = getRows();
        Collection<Product> products = dataService.getAllProducts();
        int size = rows.size();
        Assert.assertTrue(size > 0);

        int i = 0;
        for (Iterator<Product> iterator = products.iterator(); iterator
                .hasNext() && i < size;) {
            Product product = iterator.next();
            assertRowData(rows.get(i), product);
            i++;
        }

    }

    @Test
    public void selectRow() {
        doLogin();

        int index = 0;

        List<WebElement> rows = getRows();

        rows.get(index).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.className("product-form"));
        List<WebElement> buttons = form.findElements(By.className("v-button"));
        Assert.assertEquals(3, buttons.size());

        List<WebElement> fields = form
                .findElements(By.className("v-textfield"));
        Assert.assertTrue(fields.size() >= 3);

        Optional<Product> selectedProduct = dataService.getAllProducts()
                .stream().skip(index).findFirst();
        Product product = selectedProduct.get();

        checkTextField(fields.get(0), "Product name", product.getProductName(),
                true);
        checkTextField(fields.get(1), "Price",
                product.getPrice().toPlainString(), false);
        checkTextField(fields.get(2), "In Stock",
                String.valueOf(product.getStockCount()), true);

        WebElement combo = form
                .findElement(By.className("v-filterselect-input"));
        checkTextField(combo, "Availability",
                product.getAvailability().toString(), true);

        checkCategories(form, product);
    }

    @Test
    public void updateContact() {
        doLogin();

        int index = 1;

        List<WebElement> rows = getRows();

        rows.get(index).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.className("product-form"));

        List<WebElement> fields = form
                .findElements(By.className("v-textfield"));

        WebElement productName = fields.get(0);
        productName.clear();
        productName.sendKeys("Updated Product Name");

        form.findElement(By.className("primary")).click();

        checkFormLocation(form);

        rows = getRows();

        WebElement firstNameColumn = rows.get(index)
                .findElements(By.tagName("td")).get(1);
        hasText(firstNameColumn, "Updated Product Name");
    }

    @Test
    public void validationError() {
        doLogin();

        int index = 3;

        List<WebElement> rows = getRows();

        rows.get(index).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.className("product-form"));

        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));
        Assert.assertFalse(isElementPresent(By.className("v-textfield-error")));

        List<WebElement> fields = form
                .findElements(By.className("v-textfield"));

        WebElement productName = fields.get(0);
        productName.clear();
        productName.sendKeys("a");
        productName.sendKeys(Keys.TAB);

        Assert.assertTrue(isElementPresent(By.className("v-errorindicator")));
        Assert.assertTrue(isElementPresent(By.className("v-textfield-error")));
    }

    @Test
    @Ignore
    /*
     * XXX: doesn't work in PhantomJS. Works in Chrome. It looks like there is
     * some issue with screen size/responsiveness and PhantomJS.
     * 
     * No screen size issues in Chrome.
     */
    public void closeEditor() {
        doLogin();

        int index = 0;

        List<WebElement> rows = getRows();

        // select a row
        rows.get(index).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.className("product-form"));
        WebElement cancel = form.findElement(By.className("cancel"));
        cancel.click();

        // Element is not removed. It's made hidden in UI
        checkFormLocation(form);
    }

    @Test
    @Ignore
    /*
     * XXX: doesn't work in PhantomJS. Works in Chrome. It looks like there is
     * some issue with screen size/responsiveness and PhantomJS.
     * 
     * No screen size issues in Chrome.
     */
    public void deselectRow() {
        doLogin();
        int index = 0;

        List<WebElement> rows = getRows();

        // select a row
        rows.get(index).findElement(By.tagName("td")).click();
        WebElement form = findElement(By.className("product-form"));

        Assert.assertTrue(isElementPresent(By.className("product-form")));

        // deselect
        rows.get(index).findElement(By.tagName("td")).click();
        // Element is not removed. It's made hidden in UI
        checkFormLocation(form);
    }

    private void checkCategories(WebElement form, Product product) {
        WebElement categories = form
                .findElement(By.className("v-select-optiongroup"));
        WebElement captionElement = categories.findElement(By.xpath(".."))
                .findElement(By.className("v-caption"));
        Assert.assertEquals("Categories", captionElement.getText());

        List<WebElement> checkboxes = categories
                .findElements(By.className("v-checkbox"));
        Set<String> cats = checkboxes.stream().map(WebElement::getText)
                .collect(Collectors.toSet());
        Set<String> allCats = dataService.getAllCategories().stream()
                .map(Category::getName).collect(Collectors.toSet());
        Assert.assertEquals(allCats, cats);
        Map<String, Category> productCategories = product.getCategory().stream()
                .collect(Collectors.toMap(Category::getName,
                        Function.identity()));

        checkboxes.stream().forEach(
                checkbox -> checkCategory(checkbox, productCategories));
    }

    private void checkCategory(WebElement checkbox,
            Map<String, Category> productCategories) {
        String text = checkbox.getText();
        Category category = productCategories.get(text);
        if (category == null) {
            Assert.assertFalse(
                    checkbox.findElement(By.tagName("input")).isSelected());
        } else {
            Assert.assertTrue(
                    checkbox.findElement(By.tagName("input")).isSelected());
        }
    }

    private List<WebElement> getRows() {
        WebElement table = findElement(By.className("v-grid"));

        List<WebElement> bodies = table.findElements(By.tagName("tbody"));
        List<WebElement> rows = bodies.get(bodies.size() - 1)
                .findElements(By.tagName("tr"));
        return rows;
    }

    private void checkTextField(WebElement field, String caption, String value,
            boolean exact) {
        WebElement captionElement = field.findElement(By.xpath(".."))
                .findElement(By.xpath(".."))
                .findElement(By.className("v-caption"));
        Assert.assertEquals(caption, captionElement.getText());
        if (exact) {
            Assert.assertEquals(value, field.getAttribute("value"));
        } else {
            Assert.assertTrue(field.getAttribute("value").startsWith(value));
        }
    }

    private void doLogin() {
        PasswordFieldElement pwd = $(PasswordFieldElement.class).first();
        pwd.sendKeys("foo");

        findElement(By.className("friendly")).click();
    }

    private void assertRowData(WebElement row, Product product) {
        List<WebElement> columns = row.findElements(By.tagName("td"));
        Assert.assertEquals(6, columns.size());
        hasText(columns.get(0), String.valueOf(product.getId()));
        hasText(columns.get(1), product.getProductName());
        hasText(columns.get(2), String.valueOf(product.getPrice()));
        hasText(columns.get(3), product.getAvailability().toString());
        int stockCount = product.getStockCount();
        if (stockCount == 0) {
            hasText(columns.get(4), ">-<");
        } else {
            hasText(columns.get(4), String.valueOf(stockCount));
        }
        product.getCategory().stream()
                .forEach(cat -> hasText(columns.get(5), cat.getName()));
    }

    private void checkFormLocation(WebElement form) {
        Point location = form.getLocation();
        Assert.assertTrue(location
                .getX() >= getDriver().manage().window().getSize().width - 1);
    }
}
