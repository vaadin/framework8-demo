package com.vaadin.tutorial.todomvc;

import java.util.EnumSet;

import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;

public class TodoViewImpl extends VerticalLayout implements TodoView {

    private static final String WIDTH = "500px";

    private final TodoPresenter presenter;

    private Grid<Todo> grid;
    private HorizontalLayout bottomBar;
    private Label itemCountLabel;
    private TextField newTodoField;
    private Button clearCompleted;

    private boolean allCompleted;

    private Button markAllDoneButton;

    private Todo currentlyEditedTodo;

    private EnterPressHandler newTodoFieldEnterPressHandler;

    private ConfigurableFilterDataProvider<Todo, Void, TaskFilter> filterDataProvider;

    public TodoViewImpl() {

        setWidth("100%");
        setDefaultComponentAlignment(Alignment.TOP_CENTER);
        setMargin(false);
        setSpacing(false);

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

        markAllDoneButton = new Button(VaadinIcons.CHEVRON_DOWN);
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
        // this in editTodo()
        newTodoFieldEnterPressHandler = new EnterPressHandler(
                this::onNewTodoFieldEnter);
        newTodoField.addShortcutListener(newTodoFieldEnterPressHandler);

        topBar.addComponents(markAllDoneButton, newTodoField);
        topBar.setExpandRatio(newTodoField, 1);
        topBar.setSpacing(false);

        addComponent(topBar);
    }

    private void initGrid() {
        grid = new Grid<>();
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setHeight(null);
        grid.setDetailsGenerator(this::createTodoEditor);
        grid.setStyleGenerator(this::createStyle);

        Grid.Column<Todo, String> completeButtonColumn = grid
                .addColumn(t -> "",
                        new ButtonRenderer<>(event -> presenter.markCompleted(
                                event.getItem(), !event.getItem()
                                        .isCompleted())));
        completeButtonColumn.setWidth(80);

        Grid.Column<Todo, String> todoStringColumn = grid.addColumn(
                Todo::getText,
                new ButtonRenderer<>(e -> editTodo(e.getItem())));
        todoStringColumn.setExpandRatio(1);

        Grid.Column<Todo, String> deleteButtonColumn = grid.addColumn(t -> "",
                new ButtonRenderer<>(
                        event -> presenter.delete(event.getItem())));
        deleteButtonColumn.setWidth(60);
        grid.removeHeaderRow(0);

        addComponent(grid);
    }

    private void initBottomBar() {
        itemCountLabel = new Label();
        itemCountLabel.setId("count");
        itemCountLabel.setWidth(13, Unit.EX);

        RadioButtonGroup<TaskFilter> filters = new RadioButtonGroup<>(null,
                EnumSet.allOf(TaskFilter.class));
        filters.setItemCaptionGenerator(TaskFilter::getText);
        filters.setId("filters");
        filters.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        filters.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        filters.setValue(TaskFilter.ALL);
        filters.addValueChangeListener(event -> {
            filterDataProvider.setFilter(event.getValue());
            presenter.refreshView();
        });

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
        bottomBar.setComponentAlignment(filters, Alignment.TOP_LEFT);
        bottomBar.setVisible(false);
        bottomBar.setWidth(WIDTH);

        addComponents(bottomBar);
    }

    @Override
    public void refresh() {
        grid.getDataProvider().refreshAll();
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

    @Override
    public void setDataProvider(TodoJDBCDataProvider dataProvider) {
        filterDataProvider = dataProvider.withConfigurableFilter();
        grid.setDataProvider(filterDataProvider);
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
