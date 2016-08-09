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
package com.vaadin.demo.testutil;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchDriverProxy;
import com.vaadin.testbench.TestBenchTestCase;

/**
 * TestBench test class which sets up URLs according to how demo projects are
 * configured.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractDemoTest extends TestBenchTestCase {

    /**
     * The rule used for screenshot failures.
     */
    @Rule
    public ScreenshotOnFailureRule screenshotOnFailure = new ScreenshotOnFailureRule(
            this, true);

    /**
     * Default port for test server, possibly overridden with system property.
     */
    private static final String DEFAULT_SERVER_PORT = "8888";

    /** System property key for the test server port. */
    public static final String SERVER_PORT_PROPERTY_KEY = "serverPort";
    /**
     * Server port resolved by system property
     * {@value #SERVER_PORT_PROPERTY_KEY} or the default
     * {@value #DEFAULT_SERVER_PORT}.
     */
    public static final String SERVER_PORT = System
            .getProperty(SERVER_PORT_PROPERTY_KEY, DEFAULT_SERVER_PORT);

    private String hostnameAndPort = "http://localhost:" + SERVER_PORT;

    /**
     * Setup the PhantomJS driver.
     */
    @Before
    public void setupDriver() {
        setupPhantomJsDriver();
    }

    /**
     * Gets the absolute path to the test, starting with a "/".
     *
     * @return the path to the test, appended to {@link #getRootURL()} for the
     *         full test URL.
     */
    protected String getTestPath() {
        return "/";
    }

    /**
     * Returns the URL to the root of the server, e.g. "http://localhost:8888"
     *
     * @return the URL to the root
     */
    protected String getRootURL() {
        return hostnameAndPort;
    }

    protected void open() {
        open((String[]) null);
    }

    protected void open(String... parameters) {
        String url = getTestURL(parameters);

        getDriver().get(url);
    }

    /**
     * Returns the URL to be used for the test.
     *
     * @param parameters
     *            query string parameters to add to the url
     *
     * @return the URL for the test
     */
    protected String getTestURL(String... parameters) {
        String url = getRootURL();
        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        url = url + getTestPath();

        if (parameters != null && parameters.length != 0) {
            if (!url.contains("?")) {
                url += "?";
            } else {
                url += "&";
            }

            url += Arrays.stream(parameters).collect(Collectors.joining("&"));
        }

        return url;
    }

    /**
     * Executes the given JavaScript.
     *
     * @param script
     *            the script to execute
     * @param args
     *            optional arguments for the script
     * @return whatever
     *         {@link org.openqa.selenium.JavascriptExecutor#executeScript(String, Object...)}
     *         returns
     */
    protected Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) getDriver()).executeScript(script, args);
    }

    /**
     * Sets up the test to run using Phantom JS.
     *
     */
    protected void setupPhantomJsDriver() {
        setupPhantomJsDriver(new DesiredCapabilities());
    }

    /**
     * Sets up the test to run using Phantom JS with the additional capabilities
     * specified.
     *
     * @param extraCapabilities
     *            additional capabilities to pass to the driver
     */
    protected void setupPhantomJsDriver(Capabilities extraCapabilities) {
        DesiredCapabilities cap = DesiredCapabilities.phantomjs();
        cap.merge(extraCapabilities);
        FixedPhantomJSDriver driver = new FixedPhantomJSDriver(cap);
        setDriver(driver);
        driver.setTestBenchDriverProxy(getDriver());
    }

    @Override
    public TestBenchDriverProxy getDriver() {
        return (TestBenchDriverProxy) super.getDriver();
    }

    /**
     * Waits up to 10s for the given condition to become true. Use e.g. as
     * {@link #waitUntil(ExpectedCondition)}.
     *
     * @param condition
     *            the condition to wait for to become true
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntil(ExpectedCondition<T> condition) {
        waitUntil(condition, 10);
    }

    /**
     * Waits the given number of seconds for the given condition to become true.
     * Use e.g. as {@link #waitUntil(ExpectedCondition)}.
     *
     * @param condition
     *            the condition to wait for to become true
     * @param timeoutInSeconds
     *            the number of seconds to wait
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntil(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        new WebDriverWait(getDriver(), timeoutInSeconds).until(condition);
    }

    /**
     * Waits up to 10s for the given condition to become false. Use e.g. as
     * {@link #waitUntilNot(ExpectedCondition)}.
     *
     * @param condition
     *            the condition to wait for to become false
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntilNot(ExpectedCondition<T> condition) {
        waitUntilNot(condition, 10);
    }

    /**
     * Waits the given number of seconds for the given condition to become
     * false. Use e.g. as {@link #waitUntilNot(ExpectedCondition)}.
     *
     * @param condition
     *            the condition to wait for to become false
     * @param timeoutInSeconds
     *            the number of seconds to wait
     * @param <T>
     *            the return type of the expected condition
     */
    protected <T> void waitUntilNot(ExpectedCondition<T> condition,
            long timeoutInSeconds) {
        waitUntil(ExpectedConditions.not(condition), timeoutInSeconds);
    }

    protected void waitForElementPresent(final By by) {
        waitUntil(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected void waitForElementNotPresent(final By by) {
        waitUntil(input -> input.findElements(by).isEmpty());
    }

    protected void waitForElementVisible(final By by) {
        waitUntil(ExpectedConditions.visibilityOfElementLocated(by));
    }

    /**
     * Checks if the given element has the given class name.
     *
     * Matches only full class names, i.e. has ("foo") does not match
     * class="foobar"
     *
     * @param element
     *            the element to test
     * @param className
     *            the class names to match
     * @return <code>true</code> if matches, <code>false</code> if not
     */
    protected boolean hasCssClass(WebElement element, String className) {
        String classes = element.getAttribute("class");
        if (classes == null || classes.isEmpty()) {
            return className == null || className.isEmpty();
        }
        return Stream.of(classes.split(" ")).anyMatch(className::equals);
    }

    /**
     * Assert that the two elements are equal.
     * <p>
     * Can be removed if https://dev.vaadin.com/ticket/18484 is fixed.
     *
     * @param expectedElement
     *            the expected element
     * @param actualElement
     *            the actual element
     */
    protected static void assertEquals(WebElement expectedElement,
            WebElement actualElement) {
        WebElement unwrappedExpected = expectedElement;
        WebElement unwrappedActual = actualElement;
        while (unwrappedExpected instanceof WrapsElement) {
            unwrappedExpected = ((WrapsElement) unwrappedExpected)
                    .getWrappedElement();
        }
        while (unwrappedActual instanceof WrapsElement) {
            unwrappedActual = ((WrapsElement) unwrappedActual)
                    .getWrappedElement();
        }
        Assert.assertEquals(unwrappedExpected, unwrappedActual);
    }

    /**
     * Checks that the {@code element} has the {@code text} as a part of its
     * inner HTML.
     * 
     * @param element
     *            a DOM element
     * @param text
     *            a text to check against
     */
    protected boolean hasText(WebElement element, String text) {
        return element.getAttribute("innerHTML").contains(text);
    }

}
