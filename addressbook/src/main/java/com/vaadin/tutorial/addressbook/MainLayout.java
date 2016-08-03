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

import com.vaadin.annotations.DesignRoot;
import com.vaadin.tutorial.addressbook.backend.Contact;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * @author Vaadin Ltd
 *
 */
@DesignRoot
public class MainLayout extends HorizontalLayout {

    // ContactForm is an example of a custom component class
    private ContactForm contactForm;

    private LeftPanel left;

    @Override
    public void attach() {
        super.attach();

        if (contactForm == null) {
            Design.read(this);
            configureComponents();
        }
    }

    /*
     * Choose the design patterns you like.
     *
     * It is good practice to have separate data access methods that handle the
     * back-end access and/or the user interface updates. You can further split
     * your code into classes to easier maintenance. With Vaadin you can follow
     * MVC, MVP or any other design pattern you choose.
     */
    void refreshContacts() {
        refreshContacts(null);
    }

    void deselect() {
        left.deselect();
    }

    private void configureComponents() {
        left.addEditListener(() -> contactForm.edit(new Contact()));
        left.addFilterListener(this::refreshContacts);
        left.addSelectionListener(contactForm::edit);

        refreshContacts();
    }

    private void refreshContacts(String stringFilter) {
        if (stringFilter == null) {
            left.refresh();
        } else {
            left.refresh(stringFilter);
        }
        contactForm.setVisible(false);
    }

}
