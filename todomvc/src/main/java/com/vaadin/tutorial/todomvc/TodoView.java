package com.vaadin.tutorial.todomvc;

import com.vaadin.server.data.DataProvider;

import java.util.function.Supplier;

public interface TodoView {

    void refresh();

    void updateCounters(int completed, int active);

    void setDataProvider(TodoJDBCDataProvider dataProvider);
}
