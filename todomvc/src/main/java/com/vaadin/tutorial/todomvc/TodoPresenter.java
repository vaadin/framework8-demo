package com.vaadin.tutorial.todomvc;

public class TodoPresenter {

    private TodoView view;

    private TodoModel model = new TodoModel();

    private Completion completionFilter = Completion.ALL;

    public TodoPresenter(TodoView view) {
        this.view = view;
        view.setDataSource(model.getDataSourceAll());
        view.updateCounters(model.getCompleted(), model.getActive());
    }

    public void markCompleted(Todo todo, boolean completed) {
        todo.setCompleted(completed);
        model.persist(todo);
        view.updateCounters(model.getCompleted(), model.getActive());
        refreshView();
    }

    public void updateTodo(Todo todo) {
        model.persist(todo);
        refreshView();
    }

    public void add(Todo todo) {
        model.persist(todo);
        view.updateCounters(model.getCompleted(), model.getActive());

        refreshView();
    }

    public void delete(Todo todo) {
        model.drop(todo);
        view.updateCounters(model.getCompleted(), model.getActive());

        refreshView();
    }

    public void clearCompleted() {
        model.clearCompleted();

        view.updateCounters(model.getCompleted(), model.getActive());
        refreshView();
    }

    public void markAllCompleted(boolean completed) {
        model.markAllCompleted(completed);
        view.updateCounters(model.getCompleted(), model.getActive());
        refreshView();
    }

    public void filterTodos(Completion completionFilter) {
        this.completionFilter = completionFilter;
        refreshView();
    }

    private void refreshView() {
/* TODO move to DataSource level when filtering is supported */
        switch (completionFilter) {
            case ACTIVE:
                view.setDataSource(model.getDataSourceActive());
                break;
            case COMPLETED:
                view.setDataSource(model.getDataSourceCompleted());
                break;
            default:
                view.setDataSource(model.getDataSourceAll());
        }
        view.refresh();
    }
}
