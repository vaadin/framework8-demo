package com.vaadin.framework8.samples.crud;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.framework8.samples.SampleUI;
import com.vaadin.framework8.samples.backend.DataService;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
@SpringComponent
public class SampleCrudLogic implements Serializable {

    private SampleCrudView view;

    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
    @SpringComponent
    public static class SampleCrudLogicFactory {

        @Autowired
        private ApplicationContext context;

        public SampleCrudLogic createLogic(SampleCrudView view) {
            SampleCrudLogic logic = context.getBean(SampleCrudLogic.class);
            logic.init(view);
            return logic;
        }
    }

    @Autowired
    private DataService dataService;

    private SampleCrudLogic() {
    }

    public void init() {
        editProduct(null);
        // Hide and disable if not admin
        if (!SampleUI.get().getAccessControl().isUserInRole("admin")) {
            view.setNewProductEnabled(false);
        }
    }

    public void cancelProduct() {
        setFragmentParameter("");
        view.clearSelection();
        view.editProduct(null);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String productId) {
        String fragmentParameter;
        if (productId == null || productId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = productId;
        }

        Page page = SampleUI.get().getPage();
        page.setUriFragment(
                "!" + SampleCrudView.VIEW_NAME + "/" + fragmentParameter,
                false);
    }

    public void enter(String productId) {
        if (productId != null && !productId.isEmpty()) {
            if (productId.equals("new")) {
                newProduct();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    int pid = Integer.parseInt(productId);
                    Product product = findProduct(pid);
                    view.selectRow(product);
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    private Product findProduct(int productId) {
        return dataService.getProduct(productId);
    }

    public void saveProduct(Product product) {
        view.showSaveNotification(product.getProductName() + " ("
                + product.getId() + ") updated");
        view.clearSelection();
        view.editProduct(null);
        view.updateProduct(product);
        setFragmentParameter("");
    }

    public void deleteProduct(Product product) {
        dataService.deleteProduct(product.getId());
        view.showSaveNotification(product.getProductName() + " ("
                + product.getId() + ") removed");

        view.clearSelection();
        view.editProduct(null);
        view.removeProduct(product);
        setFragmentParameter("");
    }

    public void editProduct(Product product) {
        if (product == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(product.getId() + "");
        }
        view.editProduct(product);
    }

    public void newProduct() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editProduct(new Product());
    }

    public void rowSelected(Product product) {
        if (SampleUI.get().getAccessControl().isUserInRole("admin")) {
            view.editProduct(product);
        }
    }

    private void init(SampleCrudView view) {
        this.view = view;
    }
}
