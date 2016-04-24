package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.condition.StoryTrigger;

public class StateTransition {

    private static final Logger log = LoggerFactory.getLogger(StateTransition.class);

    private int target;

    private StoryTrigger[] triggers;

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public StoryTrigger[] getTriggers() {
        return triggers;
    }

    public void setTriggers(String[] triggers) {
        this.triggers = new StoryTrigger[triggers.length];
        for (int i = 0; i < triggers.length; i++) {
            this.triggers[i] = new StoryTrigger();
            this.triggers[i].setConditions(triggers[i]);
        }
    }
}
