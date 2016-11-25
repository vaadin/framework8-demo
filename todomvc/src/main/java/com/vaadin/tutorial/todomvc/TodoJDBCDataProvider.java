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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

/**
 * JDBC DataProvider implementation with filtering supported.
 */
class TodoJDBCDataProvider extends PreparedJDBCDataProvider<Todo, Supplier<TaskFilter>> {

    public TodoJDBCDataProvider(
            Connection connection) throws SQLException {
        super(connection, "SELECT * FROM todo WHERE completed = ? or ?", resultSet ->
        {
            Todo todo = new Todo();
            todo.setId(resultSet.getInt("id"));
            todo.setText(resultSet.getString("text"));
            todo.setCompleted(resultSet.getBoolean("completed"));
            return todo;
        });
    }

    @Override
    protected ResultSet rowCountStatement(Connection connection,
            Query<Supplier<TaskFilter>> query) throws SQLException {
        return runStatement(sizeStatement, query);
    }

    @Override
    protected ResultSet resultSetStatement(
            Query<Supplier<TaskFilter>> query) throws SQLException {
        return runStatement(resultSetStatement, query);
    }

    private synchronized ResultSet runStatement(PreparedStatement preparedStatement,
            Query<Supplier<TaskFilter>> query) throws SQLException {
        TaskFilter taskFilter = query.getFilter().map(Supplier::get).orElse(TaskFilter.ALL);
        switch (taskFilter) {
            case ACTIVE:
                preparedStatement.setBoolean(1, false);
                preparedStatement.setBoolean(2, false);
                break;
            case COMPLETED:
                preparedStatement.setBoolean(1, true);
                preparedStatement.setBoolean(2, false);
                break;
            default:
                preparedStatement.setBoolean(1, true);
                preparedStatement.setBoolean(2, true);
                break;

        }
        return preparedStatement.executeQuery();
    }
}
