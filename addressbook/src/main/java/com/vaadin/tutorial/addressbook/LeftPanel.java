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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.DateToLongConverter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.renderers.DateRenderer;

/**
 * @author Vaadin Ltd
 *
 */
@DesignRoot
public class LeftPanel extends VerticalLayout {

    private ActionsBar actions;

    /*
     * Hundreds of widgets. Vaadin's user interface components are just Java
     * objects that encapsulate and handle cross-browser support and
     * client-server communication. The default Vaadin components are in the
     * com.vaadin.ui package and there are over 500 more in
     * vaadin.com/directory.
     */
    private Grid contactList;

    public LeftPanel() {
        Design.read(this);

        contactList
                .setContainerDataSource(new BeanItemContainer<>(Contact.class));
        contactList.setColumnOrder("firstName", "lastName", "email");
        contactList.removeColumn("id");
        contactList.removeColumn("birthDate");
        contactList.removeColumn("phone");
        contactList.setSelectionMode(Grid.SelectionMode.SINGLE);

        Column timestamp = contactList.getColumn("createdTimestamp");
        timestamp.setRenderer(
                new DateRenderer(new SimpleDateFormat("YYYY-MM-DD HH:mm:ss")));
        timestamp.setConverter(new DateToLongConverter());
        contactList.getColumn("doNotCall")
                .setConverter(new StringToBooleanConverter("DO NOT CALL", ""));
    }

    void refresh(String filter) {
        contactList.setContainerDataSource(new BeanItemContainer<>(
                Contact.class, getContactProvider().apply(filter)));
    }

    void refresh() {
        contactList
                .setContainerDataSource(new BeanItemContainer<>(Contact.class,
                        getContactProvider().apply(actions.getFilterValue())));
    }

    void addFilterListener(Consumer<String> listener) {
        actions.addFilterListener(listener);
    }

    void addEditListener(Runnable listener) {
        actions.addEditListener(listener);
    }

    void addSelectionListener(Consumer<Contact> listener) {
        contactList.addSelectionListener(
                e -> listener.accept((Contact) contactList.getSelectedRow()));
    }

    void deselect() {
        contactList.select(null);
    }

    private Function<String, List<Contact>> getContactProvider() {
        return (AddressbookUI) getUI();
    }
}
