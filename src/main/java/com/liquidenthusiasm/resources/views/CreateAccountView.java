package com.liquidenthusiasm.resources.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.views.View;

public class CreateAccountView extends View {

    private static final Logger log = LoggerFactory.getLogger(CreateAccountView.class);

    private String email;

    private String displayName;

    private String pass;

    private String passAgain;

    private String errors = null;

    public CreateAccountView() {
        super("createAccount.ftl");
        email = "";
        displayName = "";
        pass = "";
        passAgain = "";

    }

    public CreateAccountView(String email, String displayName, String pass, String passAgain) {
        this();

        this.email = email;
        this.displayName = displayName;
        this.pass = pass;
        this.passAgain = passAgain;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPass() {
        return pass;
    }

    public String getPassAgain() {
        return passAgain;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        if (errors == null)
            errors = error;
        else
            errors += "<br>" + error;
    }

    public boolean hasErrors() {
        return errors != null;
    }
}
