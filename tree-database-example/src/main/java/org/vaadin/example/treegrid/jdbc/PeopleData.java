package org.vaadin.example.treegrid.jdbc;

import com.vaadin.data.provider.AbstractHierarchicalDataProvider;
import com.vaadin.data.provider.HierarchicalQuery;
import org.vaadin.example.treegrid.jdbc.pojo.Company;
import org.vaadin.example.treegrid.jdbc.pojo.Department;
import org.vaadin.example.treegrid.jdbc.pojo.NamedItem;
import org.vaadin.example.treegrid.jdbc.pojo.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;

/**
 * Example of AbstractHierarchicalDataProvider, based on top of pure JDBC.
 */
public class PeopleData extends AbstractHierarchicalDataProvider<NamedItem, Void> {

    @Override
    public int getChildCount(HierarchicalQuery<NamedItem, Void> query) {
        NamedItem parent = query.getParent();
        int count;
        if (parent instanceof Person) {
            return 0;
        } else if (parent instanceof Department) {
            count = readInt("select count(*) from people where department_id=?", parent);
        } else if (parent instanceof Company) {
            count = readInt("select count(*) from department where company_id=?", parent);
        } else {
            count = readInt("select count(*) from company", null);
        }
        return count - query.getOffset();
    }

    @Override
    public Stream<NamedItem> fetchChildren(HierarchicalQuery<NamedItem, Void> query) {
        NamedItem parent = query.getParent();
        if (parent instanceof Person) {
            return Stream.empty();
        }
        DataRetriever<NamedItem> retriever;
        String sql;
        if (parent instanceof Department) {
            sql = "SELECT * FROM people WHERE department_id=?";
            retriever = resultSet -> new Person(resultSet.getLong("id"),
                    resultSet.getLong("department_id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString("gender")
            );
        } else if (parent instanceof Company) {
            sql = "SELECT * FROM department WHERE company_id=?";
            retriever = resultSet -> new Department(
                    resultSet.getLong("department_id"),
                    resultSet.getLong("company_id"),
                    resultSet.getString("department_name"));
        } else {
            sql = "SELECT * FROM company";
            retriever = resultSet -> new Company(resultSet.getLong("company_id"),
                    resultSet.getString("company_name"));
        }
        try (Connection connection = DBEngine.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parent != null) {
                statement.setLong(1, parent.getId());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                for(int i = 0; i < query.getOffset();i++)
                {
                    if(!resultSet.next()) return Stream.empty();
                }
                Stream.Builder<NamedItem> builder = Stream.builder();
                int limit = query.getLimit();
                for (int i = 0; i < limit && resultSet.next(); i++) {
                    builder.add(retriever.readRow(resultSet));
                }
                return builder.build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasChildren(NamedItem item) {
        return getChildCount(new HierarchicalQuery<>(null, item)) > 0;
    }

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public Object getId(NamedItem item) {
        return item.getClass().getCanonicalName() + item.getId();
    }

    @FunctionalInterface
    public interface DataRetriever<T> {
        T readRow(ResultSet resultSet) throws SQLException;
    }

    private int readInt(String sql, NamedItem parent) {
        try (Connection connection = DBEngine.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (parent != null) statement.setLong(1, parent.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
