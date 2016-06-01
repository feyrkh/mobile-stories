package com.liquidenthusiasm.action.story;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liquidenthusiasm.action.condition.StoryTrigger;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

@JsonSerialize(using = StateTransition.Serializer.class)
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

    private static final Pattern TRANSITION_PATTERN = Pattern.compile("\\s*(-?\\d+)(?:\\s+if\\s+(.+))?");

    public void validate() {
        try {
            if (triggers != null) {
                for (StoryTrigger trigger : triggers) {
                    trigger.validate();
                }
            }
        } catch (Exception e) {
            throw new ValidationException("Error while validating transition: '" + this + "'", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(target);
        if (triggers != null && triggers.length > 0) {
            sb.append(" if ");
            boolean firstTrigger = true;
            for (StoryTrigger trigger : triggers) {
                if (!firstTrigger)
                    sb.append(" | ");
                sb.append(trigger.toString());
                firstTrigger = false;
            }

        }
        return sb.toString();
    }

    @JsonCreator
    public static StateTransition fromString(String str) {
        Matcher m = TRANSITION_PATTERN.matcher(str);
        if (m.matches()) {
            String triggerStr;
            int nextState = Integer.parseInt(m.group(1));
            StateTransition retval = new StateTransition();
            retval.setTarget(nextState);
            if (m.group(2) != null) {
                triggerStr = m.group(2);
                String[] triggerGroups = triggerStr.split("\\s*\\|\\s*");
                StoryTrigger[] newTriggers = new StoryTrigger[triggerGroups.length];
                for (int i = 0; i < triggerGroups.length; i++) {
                    newTriggers[i] = StoryTrigger.fromString(triggerGroups[i]);
                }
                retval.setTriggers(newTriggers);
            }
            return retval;
        }
        throw new RuntimeException("Unable to match '" + str + "' with pattern " + TRANSITION_PATTERN);
    }

    @JsonCreator
    public static StateTransition fromInteger(int i) {
        StateTransition retval = new StateTransition();
        retval.setTarget(i);
        return retval;
    }

    public static class Serializer extends JsonSerializer<StateTransition> {

        @Override
        public void serialize(StateTransition st, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
            jsonGenerator.writeString(st.toString());
        }
    }
}
