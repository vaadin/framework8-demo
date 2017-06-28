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

import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Vaadin DataProvider over pure JDBC, base class.
 *
 * @param <T> data transfer object. Might be POJO or Map.
 * @author Vaadin Ltd
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractJDBCDataProvider<T, F>
        extends AbstractBackEndDataProvider<T, F> implements AutoCloseable {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractJDBCDataProvider.class.getName());
    protected final java.sql.Connection connection;
    protected final DataRetriever<T> jdbcReader;

    private int cachedSize = -1;

    public AbstractJDBCDataProvider(Connection connection,
                                    DataRetriever<T> jdbcReader) {
        this.connection = Objects.requireNonNull(connection);
        this.jdbcReader = Objects.requireNonNull(jdbcReader);
    }

    protected static void closeResources(List<? extends AutoCloseable> statements) {
        for (AutoCloseable closeable : statements) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Prepared closeable was closed with error", e);
            }
        }
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        if (cachedSize < 0) {
            try (ResultSet resultSet = rowCountStatement(query)) {
                resultSet.next();
                cachedSize = resultSet.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException("Size SQL query failed", e);
            }
        }
        int size = cachedSize - query.getOffset();
        if (size < 0) {
            return 0;
        }
        return Math.min(size, query.getLimit());
    }

    protected abstract ResultSet rowCountStatement(Query<T, F> query) throws SQLException;

    protected abstract ResultSet resultSetStatement(Query<T, F> query) throws SQLException;

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, F> query) {
        try (ResultSet resultSet = resultSetStatement(query)){
            try {
                resultSet.absolute(query.getOffset());
            } catch (SQLFeatureNotSupportedException e) {
                for (int i = query.getOffset(); i > 0; i--) {
                    resultSet.next();
                }
            }
            Stream.Builder<T> builder = Stream.builder();
            for(int i =0; i < query.getLimit() && resultSet.next();i++) {
                builder.add(jdbcReader.readRow(resultSet));
            }
            return builder.build();
        } catch (SQLException e) {
            throw new RuntimeException("Data SQL query failed", e);
        }
    }

    @Override
    public void refreshAll() {
        cachedSize = -1;
        super.refreshAll();
    }

    @FunctionalInterface
    public interface DataRetriever<T> {
        T readRow(ResultSet resultSet) throws SQLException;
    }
}
