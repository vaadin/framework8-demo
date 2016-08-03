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

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;

/**
 * @author Vaadin Ltd
 *
 */
@DesignRoot
public class ActionsBar extends HorizontalLayout {

    private TextField filter;
    private Button newContact;

    public ActionsBar() {
        Design.read(this);
    }

    String getFilterValue() {
        return filter.getValue();
    }

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
        filter.addTextChangeListener(e -> listener.accept(e.getText()));
    }
}
