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
package com.vaadin.demo.registration;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.demo.testutil.AbstractDemoTest;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;

/**
 * @author Vaadin Ltd
 *
 */
public class RegistrationFormIT extends AbstractDemoTest {

    @Before
    public void setUp() {
        open();
    }

    @Test
    public void noValidationMessages() {
        Assert.assertFalse(
                isElementPresent(By.className("validation-message")));
    }

    @Test
    public void validateFullName_valid() {
        TextFieldElement fullName = $(TextFieldElement.class).first();
        fullName.sendKeys("foo");
        fullName.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("", statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61452, icon.getText().charAt(0));
    }

    @Test
    public void validateFullName_notValid() {
        TextFieldElement fullName = $(TextFieldElement.class).first();
        fullName.sendKeys("foo");
        fullName.sendKeys(Keys.TAB);

        fullName.sendKeys("");
        fullName.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("Full name may not be null",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validateEmail_valid() {
        TextFieldElement email = $(TextFieldElement.class).get(1);
        email.sendKeys("foo@vaadin.com");
        email.sendKeys(Keys.TAB);

        Assert.assertFalse(
                isElementPresent(By.className("validation-message")));
    }

    @Test
    public void validateEmail_invalid() {
        TextFieldElement email = $(TextFieldElement.class).get(1);
        email.sendKeys("foo");
        email.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("The string 'foo' is not valid email address",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePhone_valid() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys(" + 354 111 222 44 66 7688");
        phone.sendKeys(Keys.TAB);

        Assert.assertFalse(
                isElementPresent(By.className("validation-message")));
    }

    @Test
    public void validatePhone_invalidTooShort() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys("+354");
        phone.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals(
                "The string '+354' is not valid phone. Phone should start from +0-9 and contain at least 10 digits",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePhone_invalidOnlyCountryCode() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys("+7");
        phone.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals(
                "The string '+7' is not valid phone. Phone should start from +0-9 and contain at least 10 digits",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePhone_invalidContainsLetters() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys("+7 dsfdsf 435345565654");
        phone.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals(
                "The string '+7 dsfdsf 435345565654' is not valid phone. Phone should start from +0-9 and contain only digits",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePasswd_valid() {
        PasswordFieldElement fullName = $(PasswordFieldElement.class).get(0);
        fullName.sendKeys("aa11bbss33ddd");
        fullName.sendKeys(Keys.TAB);

        Assert.assertFalse(
                isElementPresent(By.className("validation-message")));
    }

    @Test
    public void validatePasswd_invalidHasOnlyNumbers() {
        PasswordFieldElement fullName = $(PasswordFieldElement.class).get(0);
        fullName.sendKeys("33221144");
        fullName.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("Password must contain a letter and a number",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePasswd_invalidHasOnlyLetters() {
        PasswordFieldElement fullName = $(PasswordFieldElement.class).get(0);
        fullName.sendKeys("aabbcc");
        fullName.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("Password must contain a letter and a number",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePasswd_invalidTooShort() {
        PasswordFieldElement fullName = $(PasswordFieldElement.class).get(0);
        fullName.sendKeys("a1");
        fullName.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("Password should contain at least 6 characters",
                statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validateConfirmPasswd_valid() {
        PasswordFieldElement fullName = $(PasswordFieldElement.class).get(0);
        fullName.sendKeys("aa11bbss33ddd");
        fullName.sendKeys(Keys.TAB);

        fullName = $(PasswordFieldElement.class).get(1);
        fullName.sendKeys("aa11bbss33ddd");
        fullName.sendKeys(Keys.TAB);

        Assert.assertFalse(
                isElementPresent(By.className("validation-message")));
    }

    @Test
    public void validateConfirmPasswd_invalid() {
        PasswordFieldElement fullName = $(PasswordFieldElement.class).get(0);
        fullName.sendKeys("aa11bbss33ddd");
        fullName.sendKeys(Keys.TAB);

        fullName = $(PasswordFieldElement.class).get(1);
        fullName.sendKeys("a");
        fullName.sendKeys(Keys.TAB);

        WebElement statusMessage = findStatusMessage();
        Assert.assertEquals("Password doesn't match", statusMessage.getText());
        WebElement icon = findElement(By.className("v-icon"));
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    private WebElement findStatusMessage() {
        List<WebElement> messages = findElements(
                By.className("validation-message"));
        Assert.assertEquals(1, messages.size());
        return messages.get(0);
    }
}
