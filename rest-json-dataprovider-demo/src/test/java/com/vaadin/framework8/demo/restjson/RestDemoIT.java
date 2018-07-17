package com.vaadin.framework8.demo.restjson;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.demo.testutil.AbstractDemoTest;
import com.vaadin.testbench.By;

public class RestDemoIT extends AbstractDemoTest {

    @Before
    public void setUp() {
        open();
    }

    @Test
    public void rowHasCorrectData() {
        WebElement row = findElements(By.className("v-grid-row")).get(2);
        List<WebElement> rowCells = row
                .findElements(By.className("v-grid-cell"));

        Assert.assertEquals(7, rowCells.size());

        String[] data = { "andrea", "johansen", "andrea.johansen@example.com",
                "allinge", "7966 ridderhatten", "48559", "nordjylland" };
        IntStream.range(0, 7).forEach(i -> Assert.assertEquals(data[i],
                rowCells.get(i).getAttribute("innerHTML")));
    }
}
