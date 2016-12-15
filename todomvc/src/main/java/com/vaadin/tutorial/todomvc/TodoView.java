package com.vaadin.tutorial.todomvc;

import java.util.function.Supplier;

import com.vaadin.data.provider.DataProvider;

public interface TodoView {

    void refresh();

    void updateCounters(int completed, int active);

    void setDataProvider(TodoJDBCDataProvider dataProvider);
}
