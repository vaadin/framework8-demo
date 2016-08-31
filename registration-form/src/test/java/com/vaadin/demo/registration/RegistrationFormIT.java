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
 * Integration tests for registration form: checks how validation works.
 *
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

        assertStatusMessagePresent("");
        WebElement icon = findElement(By.className("v-icon"));
        // 61452 is a code that used in font awsome for the "selected" sign (OK)
        Assert.assertEquals(61452, icon.getText().charAt(0));
    }

    @Test
    public void validateFullName_notValid() {
        TextFieldElement fullName = $(TextFieldElement.class).first();
        fullName.sendKeys("foo");
        fullName.sendKeys(Keys.TAB);

        fullName.focus();
        fullName.clear();
        fullName.sendKeys(Keys.TAB);

        assertStatusMessagePresent("Full name may not be empty");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validateEmail_valid() {
        TextFieldElement email = $(TextFieldElement.class).get(1);
        email.sendKeys("foo@vaadin.com");
        email.sendKeys(Keys.TAB);

        Assert.assertTrue(isElementPresent(By.className("v-slot-valid")));
    }

    @Test
    public void validateEmail_invalid() {
        TextFieldElement email = $(TextFieldElement.class).get(1);
        email.sendKeys("foo");
        email.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "The string 'foo' is not a valid email address");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePhone_valid() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys(" + 354 111 222 44 66 7688");
        phone.sendKeys(Keys.TAB);

        Assert.assertTrue(isElementPresent(By.className("v-slot-valid")));
    }

    @Test
    public void validatePhone_invalidTooShort() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys("+354");
        phone.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "The string '+354' is not a valid phone number. "
                        + "Phone should start with a plus sign and contain at least 10 digits");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePhone_invalidOnlyCountryCode() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys("+7");
        phone.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "The string '+7' is not a valid phone number. "
                        + "Phone should start with a plus sign and contain at least 10 digits");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePhone_invalidContainsLetters() {
        TextFieldElement phone = $(TextFieldElement.class).get(1);
        phone.sendKeys("+7 dsfdsf 435345565654");
        phone.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "The string '+7 dsfdsf 435345565654' is not a valid phone number. "
                        + "Phone numbers should start with a plus sign followed by digits.");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePasswd_valid() {
        PasswordFieldElement password = $(PasswordFieldElement.class).get(0);
        password.sendKeys("aa11bbss33ddd");
        password.sendKeys(Keys.TAB);

        Assert.assertTrue(isElementPresent(By.className("v-slot-valid")));
    }

    @Test
    public void validatePasswd_invalidHasOnlyNumbers() {
        PasswordFieldElement password = $(PasswordFieldElement.class).get(0);
        password.sendKeys("33221144");
        password.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "Password must contain a letter and a number");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePasswd_invalidHasOnlyLetters() {
        PasswordFieldElement password = $(PasswordFieldElement.class).get(0);
        password.sendKeys("aabbcc");
        password.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "Password must contain a letter and a number");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validatePasswd_invalidTooShort() {
        PasswordFieldElement password = $(PasswordFieldElement.class).get(0);
        password.sendKeys("a1");
        password.sendKeys(Keys.TAB);

        assertStatusMessagePresent(
                "Password should contain at least 6 characters");
        WebElement icon = findElement(By.className("v-icon"));
        // 61453 is a code that used in font awsome for the "times" sign (ERROR)
        Assert.assertEquals(61453, icon.getText().charAt(0));
    }

    @Test
    public void validateConfirmPasswd_valid() {
        PasswordFieldElement password = $(PasswordFieldElement.class).get(0);
        password.sendKeys("aa11bbss33ddd");
        password.sendKeys(Keys.TAB);

        password = $(PasswordFieldElement.class).get(1);
        password.sendKeys("aa11bbss33ddd");
        password.sendKeys(Keys.TAB);

        Assert.assertTrue(isElementPresent(By.className("v-slot-valid")));
    }

    @Test
    public void validateConfirmPasswd_invalid() {
        PasswordFieldElement password = $(PasswordFieldElement.class).get(0);
        password.sendKeys("aa11bbss33ddd");
        password.sendKeys(Keys.TAB);

        password = $(PasswordFieldElement.class).get(1);
        password.sendKeys("a");
        password.sendKeys(Keys.TAB);

        assertStatusMessagePresent("Password doesn't match");
        List<WebElement> icons = findElements(By.className("v-icon"));
        // 61453 is a code that is used in font awsome for the "times" sign
        // (ERROR)
        // 61452 is similarly the font awesome char code for the "checkmark" sign
        // (VALID)
        Assert.assertEquals(61452, icons.get(0).getText().charAt(0));
        Assert.assertEquals(61453, icons.get(1).getText().charAt(0));
    }

    private void assertStatusMessagePresent(String message) {
        List<WebElement> messages = findElements(
                By.className("validation-message"));
        boolean statusMessageFound = false;
        for (WebElement messageElement : messages) {
            if (messageElement.getText().equals(message)) {
                statusMessageFound = true;
            }
        }
        Assert.assertTrue(statusMessageFound);
    }
}
