package com.vaadin.framework8.samples.crud;

import java.util.Collection;
import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.framework8.samples.backend.DataService;
import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.server.Page;
import com.vaadin.server.data.DataSource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.Field;

/**
 * A form for editing a single product.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductForm extends ProductFormDesign {

    private SampleCrudLogic viewLogic;
    private com.vaadin.v7.data.fieldgroup.BeanFieldGroup<Product> fieldGroup;

    @Autowired
    private DataService dataService;

    @SpringComponent
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public static class ProductFormFactory {

        @Autowired
        private ApplicationContext context;

        public ProductForm createForm(SampleCrudLogic logic) {
            ProductForm form = context.getBean(ProductForm.class);
            form.init(logic);
            return form;
        }
    }

    private class CommitHanlderImpl implements CommitHandler {
        @Override
        public void preCommit(CommitEvent commitEvent) throws CommitException {
        }

        @Override
        public void postCommit(CommitEvent commitEvent) throws CommitException {
            Product product = fieldGroup.getItemDataSource().getBean();
            // TODO: this should be done automatically using Binder
            product.setCategory(new HashSet<>(category.getSelectedItems()));
            dataService.updateProduct(product);
        }
    }

    private ProductForm() {
    }

    public void setCategories(Collection<Category> categories) {
        category.setDataSource(DataSource.create(categories));
    }

    public void editProduct(Product product) {
        if (product == null) {
            product = new Product();
        }
        fieldGroup.setItemDataSource(new BeanItem<>(product));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        productName.setValidationVisible(false);

        selectCategories(product);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId()
                + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
    }

    /*
     * TODO: this should be done automatically using Binder
     */
    private void selectCategories(Product product) {
        category.getSelectionModel().deselectAll();
        if (product.getCategory() != null) {
            product.getCategory().stream()
                    .forEach(category.getSelectionModel()::select);
        }
    }

    @PostConstruct
    private void init() {
        addStyleName("product-form");

        price.setConverter(new EuroConverter());

        for (Availability availabilityValue : Availability.values()) {
            availability.addItem(availabilityValue);
        }

        fieldGroup = new BeanFieldGroup<>(Product.class);
        fieldGroup.bindMemberFields(this);

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = event -> formHasChanged();
        for (Field<?> field : fieldGroup.getFields()) {
            field.addValueChangeListener(valueListener);
        }

        fieldGroup.addCommitHandler(new CommitHanlderImpl());

        save.addClickListener(event -> onSave());

        cancel.addClickListener(event -> viewLogic.cancelProduct());
        delete.addClickListener(event -> onDelete());
    }

    private void onSave() {
        try {
            fieldGroup.commit();

            // only if validation succeeds
            Product product = fieldGroup.getItemDataSource().getBean();
            viewLogic.saveProduct(product);
        } catch (CommitException e) {
            Notification n = new Notification("Please re-check the fields",
                    Type.ERROR_MESSAGE);
            n.setDelayMsec(500);
            n.show(getUI().getPage());
        }
    }

    private void onDelete() {
        Product product = fieldGroup.getItemDataSource().getBean();
        viewLogic.deleteProduct(product);
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        productName.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveProduct = false;
        BeanItem<Product> item = fieldGroup.getItemDataSource();
        if (item != null) {
            Product product = item.getBean();
            canRemoveProduct = product.getId() != -1;
        }
        delete.setEnabled(canRemoveProduct);
    }

    private void init(SampleCrudLogic logic) {
        this.viewLogic = logic;
    }

    @PostConstruct
    private void initComponents() {
        category.setItemCaptionProvider(Category::getName);
    }
}
