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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vaadin.data.provider.Query;

/**
 * JDBC DataProvider implementation with filtering supported.
 */
class TodoJDBCDataProvider extends PreparedJDBCDataProvider<Todo, TaskFilter> {

    protected final PreparedStatement resultSetStatement;
    protected final PreparedStatement sizeStatement;

    protected final PreparedStatement resultSetStatementFiltered;
    protected final PreparedStatement sizeStatementFiltered;

    public TodoJDBCDataProvider(
            Connection connection) throws SQLException {
        super(connection, resultSet ->
        {
            Todo todo = new Todo();
            todo.setId(resultSet.getInt("id"));
            todo.setText(resultSet.getString("text"));
            todo.setCompleted(resultSet.getBoolean("completed"));
            return todo;
        });

        resultSetStatementFiltered =
                openStatement("SELECT * FROM todo WHERE completed = ?");
        sizeStatementFiltered =
                openStatement("SELECT count(*) FROM todo WHERE completed = ?");

        resultSetStatement = openStatement("SELECT * FROM todo");
        sizeStatement = openStatement("SELECT count(*) FROM todo");

    }

    @Override
    protected synchronized ResultSet rowCountStatement(Connection connection,
            Query<Todo, TaskFilter> query) throws SQLException {
        TaskFilter taskFilter = obtainFilterValue(query);
        if (taskFilter == TaskFilter.ALL) {
            return sizeStatement.executeQuery();
        } else {
            sizeStatementFiltered.setBoolean(1,
                    taskFilter == TaskFilter.COMPLETED);
            return sizeStatementFiltered.executeQuery();
        }
    }

    @Override
    protected ResultSet resultSetStatement(Query<Todo, TaskFilter> query)
            throws SQLException {
        TaskFilter taskFilter = obtainFilterValue(query);
        if (taskFilter == TaskFilter.ALL) {
            return resultSetStatement.executeQuery();
        } else {
            resultSetStatementFiltered.setBoolean(1,
                    taskFilter == TaskFilter.COMPLETED);
            return resultSetStatementFiltered.executeQuery();
        }
    }

    private TaskFilter obtainFilterValue(Query<Todo, TaskFilter> query) {
        assert query.getSortOrders() == null || query.getSortOrders().isEmpty();
        return query.getFilter().orElse(TaskFilter.ALL);
    }

}
