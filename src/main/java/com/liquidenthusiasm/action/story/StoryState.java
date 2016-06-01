package com.liquidenthusiasm.action.story;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryState {

    private static final Logger log = LoggerFactory.getLogger(StoryState.class);

    private int id;

    private String heading;

    private String text;

    private int actionCost = 1;

    private StoryOption[] options;

    private StoryCall[] preSubmitCalls;

    private String[] statAdds;

    private boolean canCancel = true;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getActionCost() {
        return actionCost;
    }

    public void setActionCost(int actionCost) {
        this.actionCost = actionCost;
    }

    public StoryOption[] getOptions() {
        return options;
    }

    public void setOptions(StoryOption[] options) {
        this.options = options;
    }

    public StoryCall[] getPreSubmitCalls() {
        return preSubmitCalls;
    }

    public void setPreSubmitCalls(StoryCall[] preSubmitCalls) {
        this.preSubmitCalls = preSubmitCalls;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean getCanCancel() {
        return canCancel;
    }

    public String[] getStatAdds() {
        return statAdds;
    }

    public void setStatAdds(String[] statAdds) {
        this.statAdds = statAdds;
    }

    public void validate() throws ValidationException {

    }
}
