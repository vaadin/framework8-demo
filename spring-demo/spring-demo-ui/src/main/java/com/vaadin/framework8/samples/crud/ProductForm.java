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

import com.vaadin.data.BeanBinder;
import com.vaadin.data.BeanBinder.BeanBinding;
import com.vaadin.data.Result;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.framework8.samples.backend.DataService;
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
    private final BeanBinder<Product> binder = new BeanBinder<>(Product.class);

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
        public Result<Integer> convertToModel(String value, Locale locale) {
            Result<Integer> result = super.convertToModel(value, locale);
            return result.map(stock -> stock == null ? 0 : stock);
        }

    }

    private ProductForm() {
    }

    public void setCategories(Collection<Category> categories) {
        category.setItems(categories);
    }

    public void editProduct(Product product) {
        if (product == null) {
            product = new Product();
        }
        binder.bind(product);

        delete.setEnabled(product.getId() != -1);

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

        ((BeanBinding<?, ?, ?>) binder.forSelect(availability))
                .bind("availability");

        save.addClickListener(event -> onSave());

        cancel.addClickListener(event -> viewLogic.cancelProduct());
        delete.addClickListener(event -> onDelete());

        category.setItemCaptionGenerator(Category::getName);
        ((BeanBinding<?, ?, ?>) binder.forSelect(category)).bind("category");
        binder.forField(stockCount).withConverter(new StockPriceConverter())
                .bind("stockCount");
    }

    private void onSave() {
        Product product = binder.getBean().get();
        dataService.updateProduct(product);
        viewLogic.saveProduct(product);
    }

    private void onDelete() {
        binder.getBean().ifPresent(viewLogic::deleteProduct);
    }

    private void init(SampleCrudLogic logic) {
        this.viewLogic = logic;
    }

}
