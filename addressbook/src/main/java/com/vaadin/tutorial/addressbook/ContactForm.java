package com.vaadin.tutorial.addressbook;

import com.vaadin.classic.v8.ui.FormLayout;
import com.vaadin.classic.v8.ui.HorizontalLayout;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;

/**
 * Create custom UI Components.
 *
 * Create your own Vaadin components by inheritance and composition. This is a
 * form component inherited from FormLayout. Use new Binder(Bean.class) and
 * binder.bindInstanceFields(form), to bind data fields from DTO to UI fields.
 * Similarly named field by naming convention or customized with @PropertyId
 * annotation.
 *
 * @author Vaadin Ltd
 */
public class ContactForm extends FormLayout {

    private TextField firstName = new TextField("First");
    private TextField lastName = new TextField("Last");
    private TextField phone = new TextField("Phone");
    private TextField email = new TextField("Email");
    private DatePicker birthDate = new DatePicker("Birthdate");
    private Checkbox doNotCall = new Checkbox("Do not call");
    protected Button save = new Button("save");
    protected Button cancel  = new Button("cancel");


    private final Binder<Contact> binder = new Binder<>();
    private Contact contactBeingEdited;

    public ContactForm() {
        add(firstName, lastName, phone, email, birthDate, doNotCall, new HorizontalLayout(save, cancel));

        final SerializablePredicate<String> phoneOrEmailPredicate = v -> !phone
                .getValue().trim().isEmpty()
                || !email.getValue().trim().isEmpty();

        Binding<Contact, String> emailBinding = binder.forField(email)
                .withValidator(phoneOrEmailPredicate,
                        "Both phone and email cannot be empty")
                .withValidator(new EmailValidator("Incorrect email address"))
                .bind(Contact::getEmail, Contact::setEmail);

        Binding<Contact, String> phoneBinding = binder.forField(phone)
                .withValidator(phoneOrEmailPredicate,
                        "Both phone and email cannot be empty")
                .bind(Contact::getPhone, Contact::setPhone);

        // Trigger cross-field validation when the other field is changed
        email.addValueChangeListener(event -> phoneBinding.validate());
        phone.addValueChangeListener(event -> emailBinding.validate());

        firstName.setRequiredIndicatorVisible(true);
        lastName.setRequiredIndicatorVisible(true);

        binder.bind(firstName, Contact::getFirstName, Contact::setFirstName);
        binder.bind(lastName, Contact::getLastName, Contact::setLastName);
        binder.bind(doNotCall, Contact::isDoNotCall, Contact::setDoNotCall);
        binder.bind(birthDate, Contact::getBirthDate, Contact::setBirthDate);

        /*
         * Highlight primary actions.
         *
         * With Vaadin built-in styles you can highlight the primary save button
         * and give it a keyboard shortcut for a better UX.
         */
        save.setThemeName("primary");

        save.addClickListener(this::save);
        cancel.addClickListener(this::cancel);

        setVisible(false);
    }

    void edit(Contact contact) {
        contactBeingEdited = contact;
        if (contact != null) {
            binder.readBean(contact);
            firstName.focus();
        }
        setVisible(contact != null);
    }

    public void save(ClickEvent event) {
        if (binder.writeBeanIfValid(contactBeingEdited)) {
            ContactService.getDemoService().save(contactBeingEdited);

            String msg = String.format("Saved '%s %s'.",
                    contactBeingEdited.getFirstName(),
                    contactBeingEdited.getLastName());
            Notification.show(msg);
            getAddressBook().refreshContacts();
        }

    }

    public void cancel(ClickEvent event) {
        Notification.show("Cancelled");
        getAddressBook().deselect();
    }

    public AddressbookUI getAddressBook() {
        return (AddressbookUI) UI.getCurrent().getInternals().getActiveRouterTargetsChain().get(0);

    }
}
