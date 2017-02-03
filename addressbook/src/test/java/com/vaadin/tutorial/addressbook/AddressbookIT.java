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
package com.vaadin.tutorial.addressbook;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.demo.testutil.AbstractDemoTest;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;

/**
 * @author Vaadin Ltd
 *
 */
public class AddressbookIT extends AbstractDemoTest {

    @Before
    public void setUp() {
        getDriver().resizeViewPortTo(1000, 800);
        open();
    }

    @Test
    public void tableWithData() {
        WebElement table = findElement(By.id("contactstable"));

        List<WebElement> headers = table.findElements(By.tagName("th"));
        Assert.assertEquals(5, headers.size());

        hasText(headers.get(0), "First Name");
        hasText(headers.get(1), "Last Name");
        hasText(headers.get(2), "Email");
        hasText(headers.get(3), "Created Timestamp");
        hasText(headers.get(4), "Do Not Call");

        List<WebElement> bodies = table.findElements(By.tagName("tbody"));
        List<WebElement> rows = bodies.get(bodies.size() - 1)
                .findElements(By.tagName("tr"));
        List<Contact> contacts = ContactService.getDemoService().findAll("");
        int size = rows.size();
        Assert.assertTrue(size > 0);

        for (int i = 0; i < size; i++) {
            assertRowData(i, rows, contacts);
        }
    }

    @Test
    public void selectRow() {
        int index = 0;

        List<WebElement> rows = getRows();

        rows.get(index).findElement(By.tagName("td")).click();

        List<WebElement> buttons = findElement(By.className("buttons"))
                .findElements(By.className("v-button"));
        Assert.assertEquals(2, buttons.size());

        WebElement form = findElement(By.id("contactform"));

        WebElement firstName = form.findElement(By.className("firstName"));
        WebElement lastName = form.findElement(By.className("lastName"));
        WebElement email = form.findElement(By.className("email"));
        WebElement phone = form.findElement(By.className("phone"));
        WebElement doNotCall = form.findElement(By.className("doNotCall"));

        // Remember that this is not the same ContactService as you have in web
        // App ! This is executed in different JVM. So we can compare until
        // modification is made.
        Contact contact = ContactService.getDemoService().findAll("")
                .get(index);
        Assert.assertEquals(contact.getFirstName(),
                firstName.getAttribute("value"));
        Assert.assertEquals(contact.getLastName(),
                lastName.getAttribute("value"));
        Assert.assertEquals(contact.getEmail(), email.getAttribute("value"));
        Assert.assertEquals(contact.getPhone(), phone.getAttribute("value"));

        boolean checked = hasText(doNotCall, "checked");
        if (contact.isDoNotCall()) {
            Assert.assertTrue(checked);
        } else {
            Assert.assertFalse(checked);
        }
    }

    @Test
    public void updateContact() {
        int index = 1;

        List<WebElement> rows = getRows();

        rows.get(index).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.id("contactform"));

        WebElement firstName = form.findElement(By.className("firstName"));
        firstName.clear();
        firstName.sendKeys("Updated Name");

        $(DateFieldElement.class).first().setValue("1/1/00");

        form.findElement(By.className("primary")).click();

        Assert.assertFalse(isElementPresent(By.className("contactform")));

        rows = getRows();

        WebElement firstNameColumn = rows.get(index)
                .findElement(By.tagName("td"));
        hasText(firstNameColumn, "Updated Name");
        hasText($(GridElement.class).first().getCell(index, 3), "1/1/00");
    }

    @Test
    public void validationError() {
        int index = 1;

        List<WebElement> rows = getRows();

        rows.get(index).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.id("contactform"));
        WebElement phone = form.findElement(By.className("phone"));
        WebElement email = form.findElement(By.className("email"));

        phone.clear();
        email.clear();

        waitForElementVisible(By.className("v-errorindicator"));

        email.sendKeys("test@test.com", Keys.ENTER);
        waitForElementNotPresent(By.className("v-errorindicator"));
    }

    @Test
    public void deselectRow() {
        int index = 0;

        List<WebElement> rows = getRows();

        // select a row
        rows.get(index).findElement(By.tagName("td")).click();

        List<WebElement> buttons = findElement(By.className("buttons"))
                .findElements(By.className("v-button"));
        Assert.assertEquals(2, buttons.size());

        // deselect
        rows.get(index).findElement(By.tagName("td")).click();
        Assert.assertFalse(isElementPresent(By.className("buttons")));
    }

    @Test
    public void newContactNoValidationErrors() {
        ButtonElement newContactButton = $(ButtonElement.class).get(0);

        newContactButton.click();
        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));

        getRows().get(0).findElement(By.tagName("td")).click();
        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));
        TextFieldElement phoneField = $(TextFieldElement.class).get(3);
        TextFieldElement emailField = $(TextFieldElement.class).get(4);
        phoneField.clear();
        emailField.clear();
        Assert.assertTrue(isElementPresent(By.className("v-errorindicator")));

        newContactButton.click();
        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));
    }

    @Test
    public void cancelButtonClosesForm() {
        List<WebElement> rows = getRows();
        rows.get(1).findElement(By.tagName("td")).click();
        Assert.assertTrue(isElementPresent(By.className("v-formlayout")));

        ButtonElement cancelButton = $(ButtonElement.class).get(2);
        cancelButton.click();
        Assert.assertFalse(isElementPresent(By.className("v-formlayout")));
    }

    @Test
    public void cancelButtonDiscardsChanges() {
        String firstCellText = $(GridElement.class).first().getCell(0, 0)
                .getText();

        getRows().get(0).findElement(By.tagName("td")).click();

        WebElement form = findElement(By.id("contactform"));
        WebElement firstName = form.findElement(By.className("firstName"));
        firstName.clear();
        firstName.sendKeys("Updated Name");

        ButtonElement cancelButton = $(ButtonElement.class).get(2);
        cancelButton.click();

        Assert.assertEquals(
                $(GridElement.class).first().getCell(0, 0).getText(),
                firstCellText);
    }

    private List<WebElement> getRows() {
        WebElement table = findElement(By.id("contactstable"));

        List<WebElement> bodies = table.findElements(By.tagName("tbody"));
        List<WebElement> rows = bodies.get(bodies.size() - 1)
                .findElements(By.tagName("tr"));
        return rows;
    }

    private void assertRowData(int row, List<WebElement> rows,
            List<Contact> contacts) {
        List<WebElement> columns = rows.get(row).findElements(By.tagName("td"));
        Assert.assertEquals(5, columns.size());
        hasText(columns.get(0), contacts.get(row).getFirstName());
        hasText(columns.get(1), contacts.get(row).getLastName());
        hasText(columns.get(2), contacts.get(row).getEmail());

        // We can't compare created timestamp since the service instance is not
        // the same: tests are executed in different JVM. So let's compare only
        // reliable (permanent) values

        boolean doNotCall = hasText(columns.get(4), "DO NOT CALL");
        if (contacts.get(row).isDoNotCall()) {
            Assert.assertTrue(doNotCall);
        } else {
            Assert.assertFalse(doNotCall);
        }
    }
}
