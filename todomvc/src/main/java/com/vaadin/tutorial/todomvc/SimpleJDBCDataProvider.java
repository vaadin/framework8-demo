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
 * Vaadin DataProvider over pure JDBC. Only fixed SQL statements are supported,
 * no custom filtering or sorting.
 *
 * @author Vaadin Ltd
 */
public class SimpleJDBCDataProvider<T>
        extends PreparedJDBCDataProvider<T, Void> {

    protected final PreparedStatement resultSetStatement;
    protected final PreparedStatement sizeStatement;

    public SimpleJDBCDataProvider(Connection connection, String sqlQuery,
            DataRetriever<T> jdbcReader) {
        super(connection, jdbcReader);
        resultSetStatement = openStatement(sqlQuery);

        sizeStatement = openStatement(
                "select count(*) from (" + sqlQuery + ")");
    }

    @Override
    protected ResultSet rowCountStatement(Connection connection,
            Query<T, Void> query) throws SQLException {
        assert query.getSortOrders() == null || query.getSortOrders().isEmpty();
        return sizeStatement.executeQuery();
    }

    @Override
    protected ResultSet resultSetStatement(Query<T, Void> query)
            throws SQLException {
        assert query.getSortOrders() == null || query.getSortOrders().isEmpty();
        return resultSetStatement.executeQuery();
    }

}
