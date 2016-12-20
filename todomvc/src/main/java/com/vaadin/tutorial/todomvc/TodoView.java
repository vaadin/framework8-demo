package com.vaadin.tutorial.todomvc;

public interface TodoView {

    void refresh();

    void updateCounters(int completed, int active);

    void setDataProvider(TodoJDBCDataProvider dataProvider);
}
