package com.vaadin.tutorial.addressbook;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;

/* Create custom UI Components.
 *
 * Create your own Vaadin components by inheritance and composition.
 * This is a form component inherited from FormLayout. Use
 * Use BeanFieldGroup to bind data fields from DTO to UI fields.
 * Similarly named field by naming convention or customized
 * with @PropertyId annotation.
 */
@DesignRoot
public class ContactForm extends FormLayout {

    private class PhoneEmailValidator extends AbstractStringValidator {

        PhoneEmailValidator() {
            super("Either phone or email must be filled in.");
        }

        @Override
        protected boolean isValidValue(String value) {
            return !phone.getValue().trim().isEmpty()
                    || !email.getValue().trim().isEmpty();
        }

    }

    private ContactFormActionsBar actions;

    private TextField firstName;
    private TextField lastName;
    private TextField phone;
    private TextField email;
    private DateField birthDate;
    private CheckBox doNotCall;

    public ContactForm() {
        Design.read(this);
        configureComponents();
    }

    private void configureComponents() {
        email.addValidator(new EmailValidator("Incorrect email address"));

        PhoneEmailValidator validator = new PhoneEmailValidator();
        email.addValidator(validator);
        phone.addValidator(validator);

        email.addValueChangeListener(e -> phone.markAsDirty());
        phone.addValueChangeListener(e -> email.markAsDirty());

        firstName.setRequired(true);
        lastName.setRequired(true);

        firstName.setRequiredError("Please set the first name");
        lastName.setRequiredError("Please set the last name");

        setVisible(false);
    }

    void edit(Contact contact) {
        if (contact != null) {
            actions.edit(contact);
            firstName.focus();
        }
        setVisible(contact != null);
    }

}
