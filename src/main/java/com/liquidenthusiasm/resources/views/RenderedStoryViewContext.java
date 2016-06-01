package com.liquidenthusiasm.resources.views;

import java.util.List;

import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

public class RenderedStoryViewContext {

    private final AbstractAction action;

    private final StoryView view;

    private final Coven coven;

    private final Person person;

    private final StoryInstance state;

    private final List<AbstractAction> validActions;

    public RenderedStoryViewContext(AbstractAction action, StoryView view, Coven coven, Person person, StoryInstance state,
        List<AbstractAction> validActions) {
        this.action = action;
        this.view = view;
        this.coven = coven;
        this.person = person;
        this.state = state;
        this.validActions = validActions;
        
    }

    public AbstractAction getAction() {
        return action;
    }

    public StoryView getView() {
        return view;
    }

    public Coven getCoven() {
        return coven;
    }

    public Person getPerson() {
        return person;
    }

    public StoryInstance getState() {
        return state;
    }

    public List<AbstractAction> getValidActions() {
        return validActions;
    }
}
