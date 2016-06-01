package com.liquidenthusiasm.resources.views;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import io.dropwizard.views.View;

public class GameView extends View {

    private static final Logger log = LoggerFactory.getLogger(GameView.class);

    private final Coven coven;

    public GameView(Coven coven) {
        super("game.ftl");
        this.coven = coven;
    }

    public Coven getCoven() {
        return coven;
    }
}
