package com.liquidenthusiasm.action.story;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.condition.StoryTrigger;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

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

    public void setTriggers(StoryTrigger[] triggers) {
        this.triggers = triggers;
    }

    public boolean isTriggered(Coven coven, Person person, Map<String, Object> storyState) {
        if (triggers == null) {
            return true;
        }
        for (StoryTrigger trigger : triggers) {
            if (trigger.isTriggered(coven, person, storyState))
                return true;
        }
        return false;
    }
}
