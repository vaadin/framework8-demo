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

import java.text.SimpleDateFormat;
import java.util.function.Consumer;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.legacy.data.util.converter.LegacyDateToLongConverter;
import com.vaadin.legacy.data.util.converter.LegacyStringToBooleanConverter;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.tutorial.addressbook.backend.ContactService;
import com.vaadin.ui.Button;
import com.vaadin.ui.LegacyGrid;
import com.vaadin.ui.LegacyGrid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.renderers.DateRenderer;

/**
 * @author Vaadin Ltd
 *
 */
@DesignRoot
public class LeftPanel extends VerticalLayout {

    /*
     * Hundreds of widgets. Vaadin's user interface components are just Java
     * objects that encapsulate and handle cross-browser support and
     * client-server communication. The default Vaadin components are in the
     * com.vaadin.ui package and there are over 500 more in
     * vaadin.com/directory.
     */
    private LegacyGrid contactList;
    private TextField filter;
    private Button newContact;

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
        Design.read(this);
        contactList
                .setContainerDataSource(new BeanItemContainer<>(Contact.class));
        contactList.setColumnOrder("firstName", "lastName", "email");
        contactList.removeColumn("id");
        contactList.removeColumn("birthDate");
        contactList.removeColumn("phone");
        contactList.setSelectionMode(LegacyGrid.SelectionMode.SINGLE);

        Column timestamp = contactList.getColumn("createdTimestamp");
        timestamp.setRenderer(
                new DateRenderer(new SimpleDateFormat("YYYY-MM-DD HH:mm:ss")));
        timestamp.setConverter(new LegacyDateToLongConverter());
        contactList.getColumn("doNotCall").setConverter(
                new LegacyStringToBooleanConverter("DO NOT CALL", ""));
    }

    void refresh(String filter) {
        contactList
                .setContainerDataSource(new BeanItemContainer<>(Contact.class,
                        ContactService.getDemoService().findAll(filter)));
    }

    void refresh() {
        refresh(getFilterValue());
    }

    void addSelectionListener(Consumer<Contact> listener) {
        contactList.addSelectionListener(
                e -> listener.accept((Contact) contactList.getSelectedRow()));
    }

    void deselect() {
        contactList.select(null);
    }

    String getFilterValue() {
        return filter.getValue();
    }

}
