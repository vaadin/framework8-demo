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

        binder.forField(fullNameField)
                .withValidator(
                        new NotEmptyValidator<>("Full name may not be empty"))
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                fullNameField))
                .bind(Person::getFullName, Person::setFullName);

        TextField phoneOrEmailField = new TextField();
        addToLayout(layout, phoneOrEmailField, "Phone or Email");
        binder.forField(phoneOrEmailField)
                .withValidator(new EmailOrPhoneValidator())
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                phoneOrEmailField))
                .bind(Person::getEmailOrPhone, Person::setEmailOrPhone);

        PasswordField passwordField = new PasswordField();
        addToLayout(layout, passwordField, "Password");
        passwordBinding = binder.forField(passwordField)
                .withValidator(new PasswordValidator())
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                passwordField));
        passwordField.addValueChangeListener(
                event -> confirmPasswordBinding.validate());
        passwordBinding.bind(Person::getPassword, Person::setPassword);

        PasswordField confirmPasswordField = new PasswordField();
        addToLayout(layout, confirmPasswordField, "Password again");

        confirmPasswordBinding = binder.forField(confirmPasswordField);
        confirmPasswordBinding
                .withValidator(Validator.from(this::validateConfirmPasswd,
                        "Password doesn't match"))
                .withValidationStatusHandler(
                        status -> commonStatusChangeHandler(status,
                                confirmPasswordField))
                .bind(Person::getPassword, (person, pwd) -> {
                });

        layout.addComponent(createButton());

        fullNameField.focus();

        binder.bind(new Person());
    }

    private Button createButton() {
        Button button = new Button("Sign Up", event -> save());
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        button.setWidth(WIDTH, Unit.PIXELS);
        return button;
    }

    private void commonStatusChangeHandler(ValidationStatus<?> event,
            HasValue<?> field) {
        Label statusLabel = (Label) ((AbstractTextField) field).getData();
        statusLabel.setVisible(true);
        if (ValidationStatus.Status.OK.equals(event.getStatus())) {
            statusLabel.setValue("");
            statusLabel.setIcon(FontAwesome.CHECK);
            statusLabel.getParent().addStyleName(VALID);
        } else {
            statusLabel.setIcon(FontAwesome.TIMES);
            statusLabel.setValue(event.getMessage().orElse("Unknown error"));
            statusLabel.getParent().removeStyleName(VALID);
        }
    }

    private boolean validateConfirmPasswd(String confirmPasswordValue) {
        return Objects.equals(passwordBinding.getField().getValue(),
                confirmPasswordValue);
    }

    private void save() {
        Person person = new Person();
        if (binder.saveIfValid(person)) {
            Notification.show("Registration data saved successfully",
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
