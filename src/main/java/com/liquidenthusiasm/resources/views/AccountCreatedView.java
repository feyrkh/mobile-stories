package com.liquidenthusiasm.resources.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import io.dropwizard.views.View;

public class AccountCreatedView extends View {

    private static final Logger log = LoggerFactory.getLogger(AccountCreatedView.class);

    private final Coven coven;

    public AccountCreatedView(Coven coven) {
        super("accountCreated.ftl");
        this.coven = coven;
    }

    public Coven getCoven() {
        return coven;
    }
}
