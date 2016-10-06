package com.vaadin.framework8.samples.crud;

import com.vaadin.framework8.samples.backend.DataService;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.framework8.samples.crud.ProductForm.ProductFormFactory;
import com.vaadin.framework8.samples.crud.SampleCrudLogic.SampleCrudLogicFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link SampleCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SpringView(name = SampleCrudView.VIEW_NAME)
public class SampleCrudView extends CssLayout implements View {

    public static final String VIEW_NAME = "Inventory";
    private ProductGrid grid;

    @Autowired
    private ProductFormFactory formFactory;

    @Autowired
    private DataService dataService;

    @Autowired
    private ProductDataSource dataSource;

    private ProductForm form;

    private SampleCrudLogic viewLogic;
    private Button newProduct;

    @Autowired
    private SampleCrudLogicFactory logicFactory;

    public HorizontalLayout createTopBar() {
        TextField filter = new TextField();
        filter.setStyleName("filter-textfield");
        filter.setPlaceholder("Filter");
        filter.setImmediate(true);
        filter.addValueChangeListener(
                event -> dataSource.setFilterText(event.getValue()));

        newProduct = new Button("New product");
        newProduct.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newProduct.setIcon(FontAwesome.PLUS_CIRCLE);
        newProduct.addClickListener(event -> viewLogic.newProduct());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(filter);
        topLayout.addComponent(newProduct);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(filter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void setNewProductEnabled(boolean enabled) {
        newProduct.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    public void selectRow(Product row) {
        grid.getSelectionModel().select(row);
    }

    public Product getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editProduct(Product product) {
        if (product != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            // Issue #286
            // form.setEnabled(false);
        }
        form.editProduct(product);
    }

    public void updateProduct(Product product) {
        dataSource.save(product);
        // TODO: Grid used to scroll to the updated item
    }

    public void removeProduct(Product product) {
        dataSource.delete(product);
    }

    @PostConstruct
    private void init() {
        viewLogic = logicFactory.createLogic(this);

        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new ProductGrid();
        grid.addSelectionListener(
                event -> viewLogic.rowSelected(grid.getSelectedRow()));
        grid.setDataSource(dataSource);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);

        form = formFactory.createForm(viewLogic);
        form.setCategories(dataService.getAllCategories());
        addComponent(form);

        viewLogic.init();
    }

}
