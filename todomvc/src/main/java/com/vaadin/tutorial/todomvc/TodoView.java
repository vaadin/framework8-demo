package com.vaadin.tutorial.todomvc;

import com.vaadin.server.data.DataProvider;

public interface TodoView {

    void refresh();

    void updateCounters(int completed, int active);

    /**
     * Temporary method
     * todo remove when filtering has been implemented on DataProvider level
     *
     * @param dataProvider
     *         dataProvider
     */
    void setDataProvider(DataProvider<Todo, ?> dataProvider);
}
