package com.liquidenthusiasm.resources.views;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;
import io.dropwizard.views.View;

public class RenderedStoryView extends View {

    private static final Logger log = LoggerFactory.getLogger(RenderedStoryView.class);

    private final RenderedStoryViewContext ctx;

    public RenderedStoryView(RenderedStoryViewContext renderedStoryViewContext) {
        super("storyView.ftl");
        this.ctx = renderedStoryViewContext;
    }

    public AbstractAction getAction() {
        return ctx.getAction();
    }

    public StoryView getView() {
        return ctx.getView();
    }

    public Coven getCoven() {
        return ctx.getCoven();
    }

    public Person getPerson() {
        return ctx.getPerson();
    }

    public StoryInstance getState() {
        return ctx.getState();
    }

    public List<AbstractAction> getActionList() {
        return ctx.getValidActions();
    }
}
