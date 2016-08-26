package com.vaadin.framework8.demo.rest;

import com.vaadin.tokka.data.BackEndDataSource;

public class GOTDataSource extends BackEndDataSource<GOTCharacter> {

    public GOTDataSource() {
        super(query -> GOTService.getInstance()
                .fetchCharacters(query.getOffset(), query.getLimit()));
    }
}
