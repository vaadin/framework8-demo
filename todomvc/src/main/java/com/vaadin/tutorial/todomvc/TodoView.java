package com.vaadin.tutorial.todomvc;

import java.util.List;

public interface TodoView {

    void refresh(List<Todo> todos);

    void updateCounters(int completed, int active);

}