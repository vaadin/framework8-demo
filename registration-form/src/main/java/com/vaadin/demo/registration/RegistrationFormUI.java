package com.vaadin.demo.registration;

import java.util.Objects;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationStatus;
import com.vaadin.data.ValidationStatusChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.NotEmptyValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Title("Registration Form")
@Theme("registration")
public class RegistrationFormUI extends UI {

    private static final int WIDTH = 350;

    private final Binder<Person> binder = new Binder<>();

    private Binding<Person, String, String> fullNameBinding;
    private Binding<Person, String, String> phoneOrEmailBinding;
    private Binding<Person, String, String> passwordBinding;
    private Binding<Person, String, String> confirmPasswordBinding;

    private static final String VALID = "valid";

    private void addToLayout(Layout layout, AbstractTextField textField,
            String placeHolderText) {
        textField.setPlaceholder(placeHolderText);
        Label statusMessage = new Label();
        statusMessage.setVisible(false);
        statusMessage.addStyleName("validation-message");
        textField.setData(statusMessage);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(textField);
        textField.setWidth(WIDTH, Unit.PIXELS);
        horizontalLayout.addComponent(statusMessage);
        layout.addComponent(horizontalLayout);
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        TextField fullNameField = new TextField();
        addToLayout(layout, fullNameField, "Full name");

        fullNameBinding = binder.forField(fullNameField)
                .withValidator(
                        new NotEmptyValidator<>("Full name may not be empty"))
                .withStatusChangeHandler(this::commonStatusChangeHandler);
        fullNameField
                .addValueChangeListener(event -> fullNameBinding.validate());
        fullNameBinding.bind(Person::getFullName, Person::setFullName);

        TextField phoneOrEmailField = new TextField();
        addToLayout(layout, phoneOrEmailField, "Phone or Email");
        phoneOrEmailBinding = binder.forField(phoneOrEmailField)
                .withValidator(new EmailOrPhoneValidator())
                .withStatusChangeHandler(this::commonStatusChangeHandler);
        phoneOrEmailField.addValueChangeListener(
                event -> phoneOrEmailBinding.validate());
        phoneOrEmailBinding.bind(Person::getEmailOrPhone,
                Person::setEmailOrPhone);

        PasswordField passwordField = new PasswordField();
        addToLayout(layout, passwordField, "Password");
        passwordBinding = binder.forField(passwordField)
                .withValidator(new PasswordValidator())
                .withStatusChangeHandler(this::commonStatusChangeHandler);
        passwordField.addValueChangeListener(event -> {
            passwordBinding.validate();
            confirmPasswordBinding.validate();
        });
        passwordBinding.bind(Person::getPassword, Person::setPassword);

        PasswordField confirmPasswordField = new PasswordField();
        addToLayout(layout, confirmPasswordField, "Password again");

        confirmPasswordBinding = binder.forField(confirmPasswordField);
        confirmPasswordBinding
                .withValidator(Validator.from(this::validateConfirmPasswd,
                        "Password doesn't match"))
                .withStatusChangeHandler(this::commonStatusChangeHandler)
                .bind(Person::getPassword, (person, pwd) -> {
                });
        confirmPasswordField.addValueChangeListener(event -> {
            passwordBinding.validate();
            confirmPasswordBinding.validate();
        });

        layout.addComponent(createButton());

        fullNameField.focus();
    }

    private Button createButton() {
        Button button = new Button("Sign Up", event -> save());
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        button.setWidth(WIDTH, Unit.PIXELS);
        return button;
    }

    private void commonStatusChangeHandler(ValidationStatusChangeEvent event) {
        Label statusLabel = getStatusMessageLabel(event);
        statusLabel.setVisible(true);
        if (ValidationStatus.OK.equals(event.getStatus())) {
            statusLabel.setValue("");
            statusLabel.setIcon(FontAwesome.CHECK);
            statusLabel.getParent().addStyleName(VALID);
        } else {
            statusLabel.setIcon(FontAwesome.TIMES);
            statusLabel.setValue(event.getMessage().orElse("Unknown error"));
            statusLabel.getParent().removeStyleName(VALID);
        }
    }

    private Label getStatusMessageLabel(ValidationStatusChangeEvent event) {
        HasValue<?> field = event.getSource();
        assert field instanceof AbstractTextField;
        return (Label) ((AbstractTextField) field).getData();
    }

    private boolean validateConfirmPasswd(String confirmPasswordValue) {
        return Objects.equals(passwordBinding.getField().getValue(),
                confirmPasswordValue);
    }

    private void save() {
        Person person = new Person();
        if (binder.saveIfValid(person)) {

            Notification.show("Registration data is saved",
                    String.format("Full name '%s', email or phone '%s'",
                            person.getFullName(), person.getEmailOrPhone()),
                    Type.HUMANIZED_MESSAGE);
        } else {
            Notification.show(
                    "Registration could not be saved, please check all fields",
                    Type.ERROR_MESSAGE);
        }
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = RegistrationFormUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
