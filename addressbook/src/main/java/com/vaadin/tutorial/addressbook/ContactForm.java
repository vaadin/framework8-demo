package com.vaadin.tutorial.addressbook;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.themes.ValoTheme;

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

    private TextField firstName;
    private TextField lastName;
    private TextField phone;
    private TextField email;
    private DateField birthDate;
    private CheckBox doNotCall;
    protected Button save;
    protected Button cancel;

    private final Binder<Contact> binder = new Binder<>();

    public ContactForm() {
        Design.read(this);
        configureComponents();
    }

    private void configureComponents() {

        final SerializablePredicate<String> phoneOrEmailPredicate = v -> !phone
                .getValue().trim().isEmpty()
                || !email.getValue().trim().isEmpty();

        Binding<Contact, String, String> emailBinding = binder.forField(email)
                .withValidator(phoneOrEmailPredicate,
                        "Both phone and email cannot be empty")
                .withValidator(new EmailValidator("Incorrect email address"));
        emailBinding.bind(Contact::getEmail, Contact::setEmail);

        Binding<Contact, String, String> phoneBinding = binder.forField(phone)
                .withValidator(phoneOrEmailPredicate,
                        "Both phone and email cannot be empty");
        phoneBinding.bind(Contact::getPhone, Contact::setPhone);

        // Trigger cross-field validation when the other field is changed
        email.addValueChangeListener(event -> phoneBinding.validate());
        phone.addValueChangeListener(event -> emailBinding.validate());

        firstName.setRequired(true);
        lastName.setRequired(true);

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
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        save.addClickListener(this::save);
        cancel.addClickListener(this::cancel);

        setVisible(false);
    }

    void edit(Contact contact) {
        if (contact != null) {
            binder.bind(contact);
            firstName.focus();
        } else {
            binder.unbind();
        }
        setVisible(contact != null);
    }

    public void save(Button.ClickEvent event) {
        binder.getBean().ifPresent(bean -> {
            if (binder.saveIfValid(bean)) {
                ContactService.getDemoService().save(bean);

                String msg = String.format("Saved '%s %s'.",
                        bean.getFirstName(), bean.getLastName());
                Notification.show(msg, Type.TRAY_NOTIFICATION);
                getUI().getContent().refreshContacts();
            }
        });
    }

    public void cancel(Button.ClickEvent event) {
        Notification.show("Cancelled", Type.TRAY_NOTIFICATION);
        getUI().getContent().deselect();
    }

    @Override
    public AddressbookUI getUI() {
        return (AddressbookUI) super.getUI();
    }
}
