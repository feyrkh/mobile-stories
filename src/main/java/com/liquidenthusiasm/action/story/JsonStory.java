package com.liquidenthusiasm.action.story;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.condition.StoryTrigger;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

public class JsonStory extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(JsonStory.class);

    private StoryTrigger[] storyTriggers;

    private StoryState[] storyStates;

    private Map<Integer, StoryState> storyStateIndex = new HashMap<>();

    @Override public void initializeStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story) {
        StoryState initialState = storyStates[story.getStoryPosition()];
        StoryCall[] preSubmitCalls = initialState.getPreSubmitCalls();
        if (preSubmitCalls != null) {
            for (StoryCall preSubmitCall : preSubmitCalls) {
                preSubmitCall.call(storyFunctionRepo, coven, person, story);
            }
        }

    }

    @Override
    public void advanceStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story, StoryChoice choice) {
        int oldStateIdx = story.getStoryPosition();
        if (oldStateIdx < 0 || oldStateIdx >= storyStates.length) {
            log.error("Old state index out of bounds, expected 0 to maxStateIdx=" + (storyStates.length - 1) + " but got oldStateIdx="
                + oldStateIdx);
            story.setStoryPosition(0);
            return;
        }
        StoryState oldState = storyStates[oldStateIdx];
        int maxChoiceId = oldState.getOptions().length;
        int requestedChoiceId = choice.getChoiceId();
        if (requestedChoiceId < 0 || requestedChoiceId >= maxChoiceId) {
            log.error("Requested choiceId={} out of bounds in '{}' story, expected 0 to maxChoiceId={}", requestedChoiceId, getActionName(),
                maxChoiceId - 1);
            story.addFlash("Invalid choice ID in story " + this.getActionName() + ": " + requestedChoiceId);
            return;
        }
        StoryOption chosenOption = oldState.getOptions()[requestedChoiceId];
        Map<String, String> formValues = choice.getFormValues();
        if (!validateFormValues(formValues, chosenOption.getFields(), coven, person, story)) {
            return;
        }
        if (formValues != null) {
            story.getState().putAll(formValues);
        }
        StoryCall[] postSubmitCalls = chosenOption.getPostSubmitCalls();
        if (postSubmitCalls != null) {
            for (StoryCall storyCall : postSubmitCalls) {
                storyCall.call(storyFunctionRepo, coven, person, story);
            }
        }
        for (StateTransition stateTransition : chosenOption.getTransitions()) {
            if (stateTransition.isTriggered(coven, person, story.getState())) {
                story.setStoryPosition(stateTransition.getTarget());
                break;
            }
        }
    }

    private boolean validateFormValues(Map<String, String> formValues, FieldDef[] fields, Coven coven, Person person, StoryInstance story) {
        if (fields == null || fields.length == 0)
            return true;
        boolean validatedOk = true;
        for (FieldDef field : fields) {
            if (field.getType() == FieldDef.FieldType.select) {
                String error = StoryValidator.validateSelectField(field, formValues, coven, person, story);
                if (error != null) {
                    validatedOk = false;
                    story.addFlash(error);
                    log.warn("Invalid select option in actionId={}, actionName={}: {}", getActionId(), getActionName(), error);
                }
            }
            Map<String, Object> validation = field.getValidation();
            if (validation != null && validation.size() != 0) {
                for (Map.Entry<String, Object> v : validation.entrySet()) {
                    String error = StoryValidator
                        .validate(field.getName(), field.getLabel(), v.getKey(), v.getValue(), formValues, coven, person, story);
                    if (error != null) {
                        validatedOk = false;
                        story.addFlash(error);
                    }
                }

            }
        }
        return validatedOk;

    }

    @Override public void generateStoryTextAndOptions(StoryView view, StoryInstance instance, Coven coven, Person person) {
        StoryState curState = storyStateIndex.get(instance.getStoryPosition());

        view.setHeading(curState.getHeading());
        view.setStoryText(curState.getText());
        view.setOptions(curState.getOptions());
        view.setFlash(instance.getFlash());

        view.interpolateVariables(coven, person, instance);
    }

    @Override public boolean canStartStory(Coven coven, Person person) {
        for (StoryTrigger storyTrigger : storyTriggers) {
            if (storyTrigger.isTriggered(coven, person, null)) {
                return true;
            }
        }
        return false;
    }

    public StoryTrigger[] getStoryTriggers() {
        return storyTriggers;
    }

    @JsonProperty("states")
    public StoryState[] getStoryStates() {
        return storyStates;
    }

    @JsonProperty("triggers")
    public void setStoryTriggers(StoryTrigger[] storyTriggers) {
        this.storyTriggers = storyTriggers;
    }

    @JsonProperty("states")
    public void setStoryStates(StoryState[] storyStates) {
        this.storyStates = storyStates;
        storyStateIndex.clear();
        for (StoryState storyState : storyStates) {
            storyStateIndex.put(storyState.getId(), storyState);
        }
    }
}
