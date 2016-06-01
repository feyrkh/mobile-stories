package com.liquidenthusiasm.resources.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.views.View;

public class HomeView extends View {

    private static final Logger log = LoggerFactory.getLogger(HomeView.class);

    public HomeView() {
        super("home.ftl");
    }

}
