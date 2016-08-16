package com.vaadin.demo.registration;

import java.util.Objects;
import java.util.function.Consumer;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.HasValue;
import com.vaadin.data.Result;
import com.vaadin.data.ValidationStatus;
import com.vaadin.data.ValidationStatusChangeEvent;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.NotEmptyValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
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

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);
        layout.setMargin(true);
        setContent(layout);

        layout.addComponents(createFullName(), createEmailOrPhone(),
                createPasswordField("Password", this::configurePassword),
                createConfirmPassword(), createButton());
    }

    private Component createButton() {
        Button button = new Button("Sign Up", event -> save());
        button.addStyleName(ValoTheme.BUTTON_PRIMARY);
        button.setWidth(WIDTH, Unit.PIXELS);
        return button;
    }

    private Component createEmailOrPhone() {
        return createTextField("Phone or Email",
                binding -> binding.withValidator(new EmailOrPhoneValidator())
                        .withStatusChangeHandler(
                                this::commonStatusChangeHandler)
                        .bind(Person::getEmailOrPhone,
                                Person::setEmailOrPhone));
    }

    private Component createFullName() {
        TextField field = new TextField();
        field.focus();
        return configureField(field, "Full name", binding -> binding
                .withValidator(new NotEmptyValidator<String>(
                        "Full name may not be null"))
                .withStatusChangeHandler(this::handleFullNameStatusChange)
                .bind(Person::getFullName, Person::setFullName));
    }

    private void configurePassword(Binding<Person, String, String> binding) {
        binding.withValidator(new PasswordValidator())
                .withStatusChangeHandler(this::commonStatusChangeHandler)
                .bind(Person::getPasswd, Person::setPasswd);
        passwordBinding = binding;
    }

    private Component createConfirmPassword() {
        AbstractComponent component = createPasswordField("Password again",
                this::configureConfirmPassword);
        HasValue<?> pwdField = passwordBinding.getField();
        pwdField.addValueChangeListener(
                event -> confirmPasswordBinding.validate());
        return component;
    }

    private void configureConfirmPassword(
            Binding<Person, String, String> binding) {
        binding.withValidator(Validator.from(this::validateConfirmPasswd,
                "Password doesn't match"))
                .withStatusChangeHandler(this::commonStatusChangeHandler)
                .bind(Person::getPasswd, (person, pwd) -> {
                });
        confirmPasswordBinding = binding;
    }

    private void handleFullNameStatusChange(ValidationStatusChangeEvent event) {
        Label statusLabel = getStatusMessageLabel(event);
        statusLabel.addStyleName("full-name-status");
        if (ValidationStatus.OK.equals(event.getStatus())) {
            statusLabel.setValue("");
            statusLabel.setIcon(FontAwesome.CHECK);
            statusLabel.addStyleName(VALID);
            statusLabel.setVisible(true);
        } else {
            statusLabel.setIcon(FontAwesome.TIMES);
            statusLabel.setValue(event.getMessage().get());
            statusLabel.removeStyleName(VALID);
            statusLabel.setVisible(false);
        }
    }

    private void commonStatusChangeHandler(ValidationStatusChangeEvent event) {
        Label statusLabel = getStatusMessageLabel(event);
        if (ValidationStatus.OK.equals(event.getStatus())) {
            statusLabel.setIcon(null);
            statusLabel.setValue("");
            statusLabel.setVisible(false);
        } else {
            statusLabel.setIcon(FontAwesome.TIMES);
            statusLabel.setValue(event.getMessage().get());
            statusLabel.setVisible(true);
        }
    }

    private Label getStatusMessageLabel(ValidationStatusChangeEvent event) {
        return getStatusMessageLabel(event.getSource());
    }

    private Label getStatusMessageLabel(HasValue<?> field) {
        assert field instanceof AbstractTextField;
        return getStatusMessageLabel((AbstractTextField) field);
    }

    private Label getStatusMessageLabel(AbstractTextField field) {
        return (Label) field.getData();
    }

    private boolean validateConfirmPasswd(String confirmPasswd) {
        if (confirmPasswd.isEmpty()) {
            return true;

        }
        Result<?> result = passwordBinding.validate();
        if (result.isError()) {
            return true;
        }
        HasValue<?> pwdField = passwordBinding.getField();
        return Objects.equals(pwdField.getValue(), confirmPasswd);
    }

    private AbstractComponent configureField(AbstractTextField field,
            String placeholder,
            Consumer<Binding<Person, String, String>> configureBinding) {
        field.setPlaceholder(placeholder);
        Label statusMessage = new Label();
        statusMessage.setVisible(false);
        statusMessage.addStyleName("validation-message");
        field.setData(statusMessage);
        return createRow(field, statusMessage, configureBinding);
    }

    private Component createTextField(String placeholder,
            Consumer<Binding<Person, String, String>> configureBinding) {
        TextField field = new TextField();
        field.setPlaceholder(placeholder);
        return configureField(field, placeholder, configureBinding);
    }

    private AbstractComponent createPasswordField(String placeholder,
            Consumer<Binding<Person, String, String>> configureBinding) {
        PasswordField field = new PasswordField();
        return configureField(field, placeholder, configureBinding);
    }

    private HorizontalLayout createRow(AbstractTextField field, Label infoLabel,
            Consumer<Binding<Person, String, String>> configureBinding) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(field);
        field.setWidth(WIDTH, Unit.PIXELS);
        if (infoLabel != null) {
            layout.addComponent(infoLabel);
        }
        Binding<Person, String, String> binding = binder.forField(field);
        configureBinding.accept(binding);
        field.addValueChangeListener(event -> binding.validate());
        return layout;
    }

    private void save() {
        Person person = new Person();
        binder.save(person);
        Notification.show("Registration data is saved",
                String.format("Full name '%s', email or phone '%s'",
                        person.getFullName(), person.getEmailOrPhone()),
                Type.HUMANIZED_MESSAGE);
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = RegistrationFormUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

}
