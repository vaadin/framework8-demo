/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tutorial.addressbook;

import java.util.function.Consumer;

import com.vaadin.classic.v8.ui.VerticalLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;

/**
 * @author Vaadin Ltd
 */
public class LeftPanel extends VerticalLayout {

    /*
     * Hundreds of widgets. Vaadin's user interface components are just Java
     * objects that encapsulate and handle cross-browser support and
     * client-server communication. The default Vaadin components are in the
     * com.vaadin.ui package and there are over 500 more in
     * vaadin.com/directory.
     */
    private Grid<Contact> contactList = new Grid<Contact>(Contact.class);
    private TextField filter = new TextField();
    private Button newContact = new Button("new");

    void addEditListener(Runnable editListener) {
        /*
         * Synchronous event handling.
         *
         * Receive user interaction events on the server-side. This allows you
         * to synchronously handle those events. Vaadin automatically sends only
         * the needed changes to the web page without loading a new page.
         */
        newContact.addClickListener(e -> editListener.run());

    }

    void addFilterListener(Consumer<String> listener) {
        filter.addValueChangeListener(e -> listener.accept(e.getValue()));
    }

    public LeftPanel() {
        add(filter, contactList, newContact);
    }

    void refresh(String filter) {
        contactList.setItems(ContactService.getDemoService().findAll(filter));
    }

    void refresh() {
        refresh(getFilterValue());
    }

    void addSelectionListener(Consumer<Contact> listener) {
        contactList.asSingleSelect()
                .addValueChangeListener(e -> listener.accept(e.getValue()));
    }

    void deselect() {
        Contact value = contactList.asSingleSelect().getValue();
        if (value != null) {
            contactList.getSelectionModel().deselect(value);
        }
    }

    String getFilterValue() {
        return filter.getValue();
    }

}
