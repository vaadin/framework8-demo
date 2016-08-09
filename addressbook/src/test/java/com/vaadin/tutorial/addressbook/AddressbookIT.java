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
import org.openqa.selenium.WebElement;

import com.vaadin.demo.testutil.AbstractDemoTest;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;

/**
 * @author Vaadin Ltd
 *
 */
public class AddressbookIT extends AbstractDemoTest {

    @Before
    public void setUp() {
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

        WebElement form = findElement(By.className("contactform"));

        WebElement firstName = form.findElement(By.className("firstName"));
        WebElement lastName = form.findElement(By.className("lastName"));
        WebElement email = form.findElement(By.className("email"));
        WebElement phone = form.findElement(By.className("phone"));
        WebElement doNotCall = form.findElement(By.className("doNotCall"));

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

    private List<WebElement> getRows() {
        WebElement table = findElement(By.className("contactstable"));

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

        boolean doNotCall = hasText(columns.get(4), "DO NOT CALL");
        if (contacts.get(row).isDoNotCall()) {
            Assert.assertTrue(doNotCall);
        } else {
            Assert.assertFalse(doNotCall);
        }
    }
}
