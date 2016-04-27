package com.liquidenthusiasm.action.condition;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

public class StoryTrigger {

    private static final Logger log = LoggerFactory.getLogger(StoryTrigger.class);

    private TriggerCondition[] conditions;

    public boolean isTriggered(Coven coven, Person person, Map<String, Object> storyState) {
        if (conditions == null)
            return true;
        for (TriggerCondition trigger : conditions) {
            if (!trigger.isTriggered(coven, person, storyState))
                return false;
        }
        return true;
    }

    public void setConditions(String[] lines) {
        this.conditions = new TriggerCondition[lines.length];
        for (int i = 0; i < lines.length; i++) {
            conditions[i] = TriggerCondition.parse(lines[i]);
        }
    }

    public TriggerCondition[] getConditions() {
        return conditions;
    }

    @JsonCreator
    public static StoryTrigger fromString(String andedConditions) {
        StoryTrigger st = new StoryTrigger();
        if (andedConditions != null) {
            String[] lines = andedConditions.split("\\s*&\\s*");
            st.setConditions(lines);
        }
        return st;
    }
}
