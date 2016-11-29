/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import com.vaadin.server.data.Query;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Test JDBC
 *
 * @author Vaadin Ltd
 */
public class TestDataProviderLimits {
    private static Connection conn;
    private static SimpleJDBCDataProvider<Integer> dataProvider;

    @BeforeClass
    public static void setUpAll() throws SQLException {
        DriverManager.registerDriver(org.hsqldb.jdbc.JDBCDriver.driverInstance);
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:dataproviderdb", "SA", "");
        //For safety sake
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("DROP TABLE long_table");
        } catch (SQLException ignored) {
        }
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("CREATE TABLE long_table (i INTEGER PRIMARY KEY)");
        }
        try (PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO long_table VALUES (?)")) {
            for (int i = 0; i < 100; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.executeUpdate();
            }
        }
        dataProvider = new SimpleJDBCDataProvider<>(conn, "SELECT i FROM long_table ORDER BY i",
                resultSet -> resultSet.getInt(1));
    }


    private void doRetrieveTest(int offset, int limit, int expectedFirst,
            int expectedLast) {
        Query<Integer,Void> query = new Query<>(offset, limit,
                Collections.emptyList(), null, null);
        int size = dataProvider.size(query);
        assertEquals("Response size", expectedLast - expectedFirst + 1, size);
        List<Integer> values = dataProvider.fetch(query).collect(Collectors.toList());
        assertEquals(size, values.size());
        for (int i = 0; i < values.size(); i++) {
            assertEquals(i + expectedFirst, values.get(i).intValue());
        }
    }

    @Test
    public void retrieveInfinite() {
        doRetrieveTest(0, Integer.MAX_VALUE, 0, 99);
    }

    @Test
    public void retrieve100() {
        doRetrieveTest(0, 100, 0, 99);
    }

    @Test
    public void retrieve20() {
        doRetrieveTest(0, 20, 0, 19);
    }

    @Test
    public void retrieve20Shift() {
        doRetrieveTest(5, 20, 5, 24);
    }

    @Test
    public void retrieveEndOfRange() {
        doRetrieveTest(90, 20, 90, 99);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try (Statement statement = conn.createStatement()) {
            statement.executeUpdate("DROP TABLE long_table");
        }
        dataProvider.close();
        conn.close();
    }
}
