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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Model for the database, HSQLDB dialect is used, in-memory database
 *
 * @author Vaadin Ltd
 */
public class TodoModel {

    /*
     * Basic pollable DataSource is used here. That is mandatory for any web
     * application: In case of high load application the pool limits number of
     * physical database connections. In case of low load application,
     * connection pool fixes stall jdbc connection problem.
     */
    private volatile static BasicDataSource dataSource;

    private final Connection conn;
    private final TodoJDBCDataProvider dataProvider;

    public TodoModel() {
        try {
            DriverManager
                    .registerDriver(org.hsqldb.jdbc.JDBCDriver.driverInstance);
            conn = getDataSource().getConnection();
            dataProvider = setupDataProvider();
        } catch (SQLException e) {
            throw new RuntimeException("Model initialization failed", e);
        }
    }

    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (TodoModel.class) {
                // Standard double check trick to avoid double initialization
                // in case of race conditions
                if (dataSource == null) {
                    dataSource = new BasicDataSource();
                    dataSource.setUrl("jdbc:hsqldb:mem:tododb");
                    dataSource.setUsername("SA");
                    dataSource.setPassword("");
                    try (Connection connection = dataSource.getConnection()) {
                        setupDatabase(connection);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return dataSource;
    }

    private TodoJDBCDataProvider setupDataProvider() throws SQLException {
        return new TodoJDBCDataProvider(conn);
    }

    private static void setupDatabase(Connection connection) {
        try (Statement s = connection.createStatement()) {
            s.execute(
                    "CREATE TABLE todo (id INTEGER IDENTITY PRIMARY KEY, text VARCHAR(255) , completed BOOLEAN)");
        } catch (SQLException ignored) {
            // Nothing to do here, because
            // the table already exists, re-creation failed
        }
    }

    public int getCompleted() {
        return readInteger("select count(*) from todo where COMPLETED");
    }

    private int readInteger(String sql) {
        try (Statement s = conn.createStatement()) {
            ResultSet resultSet = s.executeQuery(sql);
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Data retrieve failed(%s)", sql), e);
        }
    }

    public int getActive() {
        return readInteger("select count(*) from todo where not COMPLETED");
    }

    public TodoJDBCDataProvider getDataProvider() {
        return dataProvider;
    }

    public Todo persist(Todo todo) {
        if (todo.getId() < 0) {
            try (PreparedStatement s = conn.prepareStatement(
                    "INSERT INTO todo(id, text, completed) VALUES (NULL, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                s.setString(1, todo.getText());
                s.setBoolean(2, todo.isCompleted());
                s.executeUpdate();
                ResultSet generatedKeys = s.getGeneratedKeys();
                generatedKeys.next();
                todo.setId(generatedKeys.getInt(1));
            } catch (SQLException e) {
                throw new RuntimeException("ToDo insertion failed", e);
            }
        } else {
            try (PreparedStatement s = conn.prepareStatement(
                    "UPDATE todo SET text= ?,completed=? WHERE id = ?")) {
                s.setString(1, todo.getText());
                s.setBoolean(2, todo.isCompleted());
                s.setInt(3, todo.getId());
                s.execute();
                if (s.getUpdateCount() != 1) {
                    throw new RuntimeException(
                            "Todo update failed (non-existing id?): " + todo);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Todo update failed", e);
            }
        }
        return todo;
    }

    public void drop(Todo todo) {
        try (Statement s = conn.createStatement()) {
            s.execute("DELETE FROM todo WHERE id = " + todo.getId());
            if (s.getUpdateCount() != 1) {
                throw new RuntimeException(
                        "Deletion failed(non-existing id?): " + todo);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Deletion failed", e);
        }
    }

    public void clearCompleted() {
        try (Statement s = conn.createStatement()) {
            s.execute("DELETE FROM todo WHERE completed");
        } catch (SQLException e) {
            throw new RuntimeException("Deletion of completed items failed", e);
        }
    }

    public void markAllCompleted(boolean completed) {
        try (PreparedStatement s = conn
                .prepareStatement("UPDATE todo SET completed=?")) {
            s.setBoolean(1, completed);
            s.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

}
