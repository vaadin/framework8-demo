package com.vaadin.tutorial.todomvc;

import com.vaadin.server.data.DataSource;

public interface TodoView {

    void refresh();

    void updateCounters(int completed, int active);

    void setDataSource(DataSource<Todo> dataSource);
}