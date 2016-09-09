package com.vaadin.tutorial.todomvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TodoPresenter {

    private TodoView view;

    // TODO replace with JDBC datasource, shared with View/Grid ?
    // "Model"
    private TodoModel model = new TodoModel();

    private String filterValue;

    public static class TodoModel {
        private List<Todo> todos = new ArrayList<>();

        public int getCompleted() {
            int nr = 0;
            for (Todo t : todos) {
                if (t.isCompleted()) {
                    nr++;
                }
            }
            return nr;
        }

        public int getActive() {
            return todos.size() - getCompleted();
        }
    }

    public TodoPresenter(TodoView view) {
        this.view = view;
    }

    public void markCompleted(Todo todo, boolean completed) {
        todo.setCompleted(completed);

        view.updateCounters(model.getCompleted(), model.getActive());
        refreshView();
    }

    public void updateText(Todo todo, String value) {
        todo.setText(value);
        model.todos.set(model.todos.indexOf(todo), todo);

        refreshView();
    }

    public void add(Todo todo) {
        model.todos.add(todo);
        view.updateCounters(model.getCompleted(), model.getActive());

        refreshView();
    }

    public void delete(Todo todo) {
        model.todos.remove(todo);
        view.updateCounters(model.getCompleted(), model.getActive());

        refreshView();
    }

    public void clearCompleted() {
        for (Todo t : model.todos.toArray(new Todo[model.todos.size()])) {
            if (t.isCompleted()) {
                model.todos.remove(t);
            }
        }

        view.updateCounters(model.getCompleted(), model.getActive());
        refreshView();
    }

    public void markAllCompleted(boolean completed) {
        for (Todo t : model.todos) {
            t.setCompleted(completed);
        }

        view.updateCounters(model.getCompleted(), model.getActive());
        refreshView();
    }

    public void filterTodos(String value) {
        filterValue = value;
        refreshView();
    }

    private void refreshView() {
        if ("Active".equals(filterValue)) {
            view.refresh(
                    model.todos.stream().filter(todo -> !todo.isCompleted())
                            .collect(Collectors.toList()));
        } else if ("Completed".equals(filterValue)) {
            view.refresh(model.todos.stream().filter(todo -> todo.isCompleted())
                    .collect(Collectors.toList()));
        } else {
            view.refresh(Collections.unmodifiableList(model.todos));
        }
    }
}