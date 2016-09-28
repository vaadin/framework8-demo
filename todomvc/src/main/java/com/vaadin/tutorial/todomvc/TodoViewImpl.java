package com.vaadin.tutorial.todomvc;

import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.data.DataSource;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

public class TodoViewImpl extends VerticalLayout implements TodoView {

    private static final String WIDTH = "500px";

    private final TodoPresenter presenter;

    private Grid<Todo> grid;
    private HorizontalLayout bottomBar;
    private Label itemCountLabel;
    private TextField newTodoField;
    private Button clearCompleted;

    private boolean allCompleted;

    private OptionGroup filters;

    private Button markAllDoneButton;

    private Todo currentlyEditedTodo;

    private EnterPressHandler newTodoFieldEnterPressHandler;

    public TodoViewImpl() {

        setWidth("100%");
        setDefaultComponentAlignment(Alignment.TOP_CENTER);

        Label headerLabel = new Label("todos");
        headerLabel.addStyleName(ValoTheme.LABEL_H1);
        addComponent(headerLabel);

        initTopBar();
        initGrid();
        initBottomBar();
        presenter = new TodoPresenter(this);
    }

    private void initTopBar() {
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidth(WIDTH);

        markAllDoneButton = new Button(FontAwesome.CHEVRON_DOWN);
        markAllDoneButton.setId("mark-all-done");
        markAllDoneButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        markAllDoneButton.addClickListener(event -> {
            allCompleted = !allCompleted;
            presenter.markAllCompleted(allCompleted);
        });

        newTodoField = new TextField();
        newTodoField.setId("new-todo");
        newTodoField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
        newTodoField.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        newTodoField.setWidth("100%");
        // value might not be updated to server when quickly pressing enter
        // without this ...
        newTodoField.setValueChangeMode(ValueChangeMode.EAGER);
        newTodoField.setPlaceholder("What needs to be done?");
        newTodoField.focus(); // auto-focus
        // there can only be one shortcut listener set, so need to add/remove
        // this in editTodo(Todo)
        newTodoFieldEnterPressHandler = new EnterPressHandler(
                this::onNewTodoFieldEnter);
        newTodoField.addShortcutListener(newTodoFieldEnterPressHandler);

        topBar.addComponents(markAllDoneButton, newTodoField);
        topBar.setExpandRatio(newTodoField, 1);

        addComponent(topBar);
    }

    private void initGrid() {
        grid = new Grid<>();
        // TODO disable grid selection once supported
        grid.setHeight(null);
        grid.setDetailsGenerator(this::createTodoEditor);
        grid.setStyleGenerator(this::createStyle);
        grid.addColumn("", t -> "", new ButtonRenderer<>(
                event -> presenter.markCompleted(event.getItem(),
                        !event.getItem().isCompleted())));

        // TODO make text column expand
        grid.addColumn("", Todo::getText,
                new ButtonRenderer<>(e -> editTodo(e.getItem())));

        grid.addColumn("", t -> "", new ButtonRenderer<>(
                event -> presenter.delete(event.getItem())));

        // TODO remove header once supported
        // grid.removeHeaderRow(0);

        addComponent(grid);
    }

    private void initBottomBar() {
        itemCountLabel = new Label();
        itemCountLabel.setId("count");

        filters = new OptionGroup(null);
        filters.setId("filters");
        filters.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        filters.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        filters.setMultiSelect(false);
        filters.addItems("All", "Active", "Completed");
        filters.select("All");
        filters.addValueChangeListener(event -> presenter
                .filterTodos((String) event.getProperty().getValue()));

        clearCompleted = new Button("Clear completed");
        clearCompleted.setId("clear-completed");
        clearCompleted.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        clearCompleted.addStyleName(ValoTheme.BUTTON_SMALL);
        clearCompleted.addClickListener(event -> presenter.clearCompleted());

        bottomBar = new HorizontalLayout();
        bottomBar.setId("bottom-bar");
        bottomBar.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        bottomBar.addComponents(itemCountLabel, filters, clearCompleted);
        bottomBar.setExpandRatio(filters, 1);
        bottomBar.setVisible(false);
        bottomBar.setSpacing(true);
        bottomBar.setWidth(WIDTH);

        addComponents(bottomBar);
    }

    @Override
    public void refresh() {
        grid.getDataSource().refreshAll();
    }

    @Override
    public void updateCounters(int completed, int active) {
        bottomBar.setVisible(completed != 0 || active != 0);

        clearCompleted.setVisible(completed != 0);

        allCompleted = active == 0;
        markAllDoneButton.setStyleName("all-done", allCompleted);

        if (active > 1) {
            itemCountLabel.setValue(String.format("%1$S items left", active));
        } else {
            itemCountLabel.setValue(String.format("%1$S item left", active));
        }
    }

    /**
     * Temporary method
     * todo remove when filtering has been implemented on DataSource level
     *
     * @param dataSource dataSource
     */
    @Override
    public void setDataSource(DataSource<Todo> dataSource) {
        grid.setDataSource(dataSource);
    }

    private void onNewTodoFieldEnter() {
        String value = newTodoField.getValue().trim();
        if (!value.isEmpty()) {
            presenter.add(new Todo(value));
            newTodoField.setValue("");
        }
    }

    private void editTodo(Todo newTodo) {
        if (currentlyEditedTodo == newTodo) {
            return;
        }
        if (currentlyEditedTodo != null) {
            presenter.updateTodo(currentlyEditedTodo);
            grid.setDetailsVisible(currentlyEditedTodo, false);
            newTodoField.addShortcutListener(newTodoFieldEnterPressHandler);
        }

        currentlyEditedTodo = newTodo;
        if (currentlyEditedTodo != null) {
            newTodoField.removeShortcutListener(newTodoFieldEnterPressHandler);
            grid.setDetailsVisible(currentlyEditedTodo, true);
        }
    }

    private TextField createTodoEditor(Todo todo) {
        TextField textField = new TextField();
        textField.setId("todo-editor");
        textField.setWidth("100%");
        textField.setValue(todo.getText());
        textField.focus();
        textField.addValueChangeListener(e -> todo.setText(e.getValue()));
        textField.addShortcutListener(
                new EnterPressHandler(() -> editTodo(null)));
        textField.addBlurListener(e -> editTodo(null));
        return textField;
    }

    private String createStyle(Todo todo) {
        return todo.isCompleted() ? "done" : "";
    }

    private class EnterPressHandler extends ShortcutListener {

        private Runnable handler;

        public EnterPressHandler(Runnable handler) {
            super("", KeyCode.ENTER, new int[0]);
            this.handler = handler;
        }

        @Override
        public void handleAction(Object sender, Object target) {
            handler.run();
        }
    }

}
