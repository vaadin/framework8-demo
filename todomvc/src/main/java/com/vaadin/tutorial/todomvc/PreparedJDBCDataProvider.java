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
import java.util.ArrayList;
import java.util.List;

/**
 * Vaadin DataProvider over pure JDBC. Filtering and sorting to be implemented
 * in subclasses.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("WeakerAccess")
public abstract class PreparedJDBCDataProvider<T, F>
        extends AbstractJDBCDataProvider<T, F> {

    protected List<PreparedStatement> statements = new ArrayList<>();

    public PreparedJDBCDataProvider(Connection connection,
            DataRetriever<T> jdbcReader) {
        super(connection, jdbcReader);
    }

    @Override
    public void close() throws Exception {
        closeResources(statements);
    }

    protected PreparedStatement openStatement(String sqlQuery) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    sqlQuery, ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            statements.add(preparedStatement);
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
