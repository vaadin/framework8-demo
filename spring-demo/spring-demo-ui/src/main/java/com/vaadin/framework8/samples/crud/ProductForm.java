package com.vaadin.framework8.samples.crud;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import com.vaadin.data.Binder;
import com.vaadin.data.Result;
import com.vaadin.data.StatusChangeEvent;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.framework8.samples.backend.data.Availability;
import com.vaadin.framework8.samples.backend.data.Category;
import com.vaadin.framework8.samples.backend.data.Product;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;

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
    private final Binder<Product> binder = new Binder<>(Product.class);

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

    private static class StockPriceConverter extends StringToIntegerConverter {

        public StockPriceConverter() {
            super("Could not convert value to " + Integer.class.getName());
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            // do not use a thousands separator, as HTML5 input type
            // number expects a fixed wire/DOM number format regardless
            // of how the browser presents it to the user (which could
            // depend on the browser locale)
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            format.setDecimalSeparatorAlwaysShown(false);
            format.setParseIntegerOnly(true);
            format.setGroupingUsed(false);
            return format;
        }

        @Override
        public Result<Integer> convertToModel(String value,
                ValueContext context) {
            Result<Integer> result = super.convertToModel(value, context);
            return result.map(stock -> stock == null ? 0 : stock);
        }

    }

    private Product currentProduct;

    private ProductForm() {
    }

    public void setCategories(Collection<Category> categories) {
        category.setItems(categories);
    }

    public void editProduct(Product product) {
        currentProduct = product;
        setUpData();

        delete.setEnabled(product != null && product.getId() != -1);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId()
                + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
    }

    @PostConstruct
    private void init() {
        addStyleName("product-form");

        availability.setItems(Availability.values());

        binder.forField(price).withConverter(new EuroConverter()).bind("price");
        binder.forField(productName).bind("productName");

        binder.forField(availability).bind("availability");

        save.addClickListener(event -> onSave());

        cancel.addClickListener(event -> viewLogic.cancelProduct());
        delete.addClickListener(event -> onDelete());
        discard.addClickListener(event -> setUpData());

        category.setItemCaptionGenerator(Category::getName);
        binder.forField(category).bind("category");
        binder.forField(stockCount).withConverter(new StockPriceConverter())
                .bind("stockCount");

        binder.addStatusChangeListener(this::updateButtons);
    }

    private void onSave() {
        if (binder.writeBeanIfValid(currentProduct)) {
            viewLogic.saveProduct(currentProduct);
        }
    }

    private void onDelete() {
        if (currentProduct != null) {
            viewLogic.deleteProduct(currentProduct);
        }
    }

    private void init(SampleCrudLogic logic) {
        viewLogic = logic;
    }

    private void updateButtons(StatusChangeEvent event) {
        boolean changes = event.getBinder().hasChanges();
        boolean validationErrors = event.hasValidationErrors();

        save.setEnabled(!validationErrors && changes);
        discard.setEnabled(changes);
    }

    private void setUpData() {
        if (currentProduct != null) {
            binder.readBean(currentProduct);
        } else {
            binder.removeBean();
        }
    }

}
