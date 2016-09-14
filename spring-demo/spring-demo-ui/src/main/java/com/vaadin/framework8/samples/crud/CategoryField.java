package com.vaadin.framework8.samples.crud;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.CustomField;

/**
 * A custom Field implementation that allows selecting a set of categories using
 * checkboxes rather than multi-selection in a list/table or a TwinColSelect.
 */
public class CategoryField extends CustomField<Set<Category>> {

    private VerticalLayout options;
    private Map<Category, CheckBox> checkboxes = new HashMap<>();
    private boolean updatingField = false;

    public CategoryField() {
        options = new VerticalLayout();
    }

    public CategoryField(String caption) {
        this();
        setCaption(caption);
    }

    @Override
    protected Component initContent() {
        return options;
    }

    /**
     * Set the collection of categories among which the used can select a
     * subset.
     * 
     * @param categories
     *            all available categories
     */
    public void setOptions(Collection<Category> categories) {
        options.removeAllComponents();
        checkboxes.clear();
        for (Category category : categories) {
            CheckBox box = new CheckBox(category.getName());
            checkboxes.put(category, box);
            box.addValueChangeListener(event -> onValueChange(box, category));
            options.addComponent(box);
        }
    }

    @Override
    public Class getType() {
        return Set.class;
    }

    @Override
    protected void setInternalValue(Set<Category> newValue) {
        updatingField = true;
        super.setInternalValue(newValue);
        if (newValue != null) {
            Set<Integer> categoryIds = newValue.stream().map(Category::getId)
                    .collect(Collectors.toSet());
            checkboxes.forEach((category, checkbox) -> checkbox
                    .setValue(categoryIds.contains(category.getId())));
        } else {
            checkboxes.values().forEach(checkBox -> checkBox.setValue(false));

        }
        updatingField = false;
    }

    private void onValueChange(CheckBox box, Category category) {
        if (!updatingField) {
            Set<Category> oldCategories = getValue();
            Set<Category> categories;
            if (oldCategories != null) {
                categories = new HashSet<>(oldCategories);
            } else {
                categories = new HashSet<>();
            }
            if (box.getValue()) {
                categories.add(category);
            } else {
                categories.remove(category);
            }
            setInternalValue(categories);
        }
    }
}
