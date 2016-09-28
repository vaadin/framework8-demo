package com.vaadin.tutorial.todomvc;

import com.vaadin.server.data.DataSource;

public interface TodoView {

    void refresh();

    void updateCounters(int completed, int active);

    /**
     * Temporary method
     * todo remove when filtering has been implemented on DataSource level
     *
     * @param dataSource
     *         dataSource
     */
    void setDataSource(DataSource<Todo> dataSource);
}