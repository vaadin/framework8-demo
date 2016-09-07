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
package com.vaadin.tutorial.todomvc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.vaadin.demo.testutil.AbstractDemoTest;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;

/**
 * @author Vaadin Ltd
 *
 */
public class TodoMvcIT extends AbstractDemoTest {

    @Before
    public void setUp() {
        open();
        getDriver().resizeViewPortTo(1000, 800);
    }

    public void testMarkingTodoCompleteFromGrid() {
        // TODO depends on checkbox renderer column
    }

    public void testDeleteTodoFromGrid() {
        // TODO depends on button renderer column
    }

    public void testInlineEditingTodoText() {
        // TODO depends on selection implementation or clickable renderer or
        // whatnot
    }

    @Test
    public void testFilteringTodosWithMarkAllDoneAndClearCompleted() {
        verifyBottomBarVisible(false);

        addTodo("one");

        verifyBottomBarVisible(true);
        verifyNumberOfItemsLeft(1);
        verifyGridRows(1);
        verifyMarkAllDoneButton(false);
        verifyClearCompletedButtonVisible(false);

        addTodo("two");

        verifyBottomBarVisible(true);
        verifyNumberOfItemsLeft(2);
        verifyGridRows(2);
        verifyMarkAllDoneButton(false);
        verifyClearCompletedButtonVisible(false);

        markAllCompleted();

        verifyNumberOfItemsLeft(0);
        verifyClearCompletedButtonVisible(true);

        filterActive();
        verifyGridEmpty();

        filterCompleted();
        verifyGridRows(2);

        addTodo("three");

        verifyNumberOfItemsLeft(1);
        verifyGridRows(2);
        verifyMarkAllDoneButton(false);
        verifyClearCompletedButtonVisible(true);

        filterAll();
        verifyGridRows(3);

        filterActive();
        verifyGridRows(1);

        filterCompleted();
        verifyGridRows(2);

        markAllCompleted();
        verifyNumberOfItemsLeft(0);
        verifyGridRows(3);

        addTodo("four");
        verifyGridRows(3);
        verifyNumberOfItemsLeft(1);

        clearCompleted();
        verifyGridRows(0);
        verifyNumberOfItemsLeft(1);

        filterActive();
        verifyGridRows(1);

        filterAll();
        verifyGridRows(1);

        addTodo("five");
        verifyGridRows(2);
        verifyNumberOfItemsLeft(2);

        markAllCompleted();
        verifyGridRows(2);
        verifyNumberOfItemsLeft(0);
        verifyClearCompletedButtonVisible(true);

        markAllActive();
        verifyGridRows(2);
        verifyNumberOfItemsLeft(2);
        verifyClearCompletedButtonVisible(false);
    }

    private void verifyBottomBarVisible(boolean visible) {
        try {
            findElement(By.id("bottom-bar"));
            if (!visible) {
                Assert.fail("bottom bar should not be visible");
            }
        } catch (NoSuchElementException nsee) {
            if (visible) {
                Assert.fail("bottom should be visible");
            }
        }
    }

    private void verifyClearCompletedButtonVisible(boolean visible) {
        try {
            getClearCompletedButton();
            if (!visible) {
                Assert.fail("clear completed button should not be visible");
            }
        } catch (NoSuchElementException nsee) {
            if (visible) {
                Assert.fail("clear completed button should be visible");
            }
        }
    }

    private void verifyMarkAllDoneButton(boolean allDone) {
        Assert.assertEquals("Mark all done button in wrong state", allDone,
                getMarkAllDoneButton().getAttribute("class")
                        .contains("all-done"));
    }

    private void verifyNumberOfItemsLeft(int numberOfItems) {
        String text = Integer.toString(numberOfItems)
                + (numberOfItems > 1 ? " items left" : " item left");
        Assert.assertEquals("Items left label has invalid text", text,
                getItemsLeftLabel().getText());
    }

    private void verifyTodoFieldText(String text) {
        Assert.assertEquals("Todo textfield has invalid text", text,
                getTodoField().getValue());
    }

    private void verifyGridEmpty() {
        verifyGridRows(0);
    }

    private void verifyGridRows(int numberOfRows) {
        for (int i = 0; i < numberOfRows; i++) {
            Assert.assertTrue("Grid does not have row at index " + i,
                    getGrid().getRow(i) != null);
        }
        try {
            getGrid().getRow(numberOfRows);
            Assert.fail("Grid should not have row at index " + numberOfRows);
        } catch (NoSuchElementException nsee) {
            // expected
        }
    }

    private void addTodo(String text) {
        getTodoField().sendKeys(text);
        verifyTodoFieldText(text);
        getTodoField().sendKeys(Keys.ENTER);
        verifyTodoFieldText("");
    }

    private void markAllCompleted() {
        verifyMarkAllDoneButton(false);
        getMarkAllDoneButton().click();
        verifyMarkAllDoneButton(true);
    }

    private void markAllActive() {
        verifyBottomBarVisible(true);
        getMarkAllDoneButton().click();
        verifyMarkAllDoneButton(false);
    }

    private void clearCompleted() {
        getClearCompletedButton().click();
        verifyClearCompletedButtonVisible(false);
    }

    private void filterAll() {
        WebElement webElement = findElement(By.id("filters"))
                .findElements(By.tagName("span")).get(0);
        Assert.assertEquals("All", webElement.getText());
        webElement.findElement(By.tagName("input")).click();
    }

    private void filterActive() {
        WebElement webElement = findElement(By.id("filters"))
                .findElements(By.tagName("span")).get(1);
        Assert.assertEquals("Active", webElement.getText());
        webElement.findElement(By.tagName("input")).click();
    }

    private void filterCompleted() {
        WebElement webElement = findElement(By.id("filters"))
                .findElements(By.tagName("span")).get(2);
        Assert.assertEquals("Completed", webElement.getText());
        webElement.findElement(By.tagName("input")).click();
    }

    private GridElement getGrid() {
        return $(GridElement.class).first();
    }

    private TextFieldElement getTodoField() {
        return $(TextFieldElement.class).first();
    }

    private WebElement getItemsLeftLabel() {
        return findElement(By.id("count"));
    }

    private WebElement getMarkAllDoneButton() {
        return findElement(By.id("mark-all-done"));
    }

    private WebElement getClearCompletedButton() {
        return findElement(By.id("clear-completed"));
    }

}
