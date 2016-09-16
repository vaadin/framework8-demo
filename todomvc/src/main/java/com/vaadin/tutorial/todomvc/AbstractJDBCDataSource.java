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

import com.vaadin.server.data.AbstractDataSource;
import com.vaadin.server.data.Query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Vaadin datasource over pure JDBC, base class.
 *
 * @author Vaadin Ltd
 */
public abstract class AbstractJDBCDataSource<T> extends AbstractDataSource<T> implements AutoCloseable {
    public static final Logger LOGGER = Logger.getLogger(AbstractJDBCDataSource.class.getName());
    private final java.sql.Connection connection;
    private final Function<ResultSet, T> jdbcReader;

    private int cachedSize = -1;

    public AbstractJDBCDataSource(Connection connection,
            Function<ResultSet, T> jdbcReader) {
        this.connection = Objects.requireNonNull(connection);
        this.jdbcReader = Objects.requireNonNull(jdbcReader);
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(Query query) {
        if (cachedSize < 0) {
            try (ResultSet resultSet = rowCountStatement(connection, query)) {
                resultSet.next();
                cachedSize = resultSet.getInt(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return cachedSize;
    }

    protected abstract ResultSet rowCountStatement(
            Connection connection, Query query) throws SQLException;

    protected abstract ResultSet resultSetStatement(
            Query query) throws SQLException;

    @Override
    public Stream<T> apply(Query query) {
        try {
            ResultSet resultSet = resultSetStatement(query);
            return StreamSupport.stream(
                    new ResultSetToSpliterator(resultSet), false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void refreshAll() {
        cachedSize = -1;
        super.refreshAll();
    }

    private class ResultSetToSpliterator extends Spliterators.AbstractSpliterator<T>
            implements
            AutoCloseable {
        private final ResultSet resultSet;

        public ResultSetToSpliterator(
                ResultSet resultSet) throws SQLException {
            super(Long.MAX_VALUE, IMMUTABLE | NONNULL);
            this.resultSet = resultSet;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            try {
                if (resultSet.isClosed()) return false;
                if (!resultSet.next()) {
                    close();
                    return false;
                }
                T pojo = jdbcReader.apply(resultSet);
                action.accept(pojo);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return true;
        }

        @Override
        public void close() {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Result set was closed with exception", e);
            }
        }
    }
}
