package com.liquidenthusiasm.action.story;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liquidenthusiasm.CleanView;
import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.condition.StoryTrigger;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonStory extends AbstractAction implements CleanView {

    private static final Logger log = LoggerFactory.getLogger(JsonStory.class);

    private StoryTrigger[] enableTriggers;

    private StoryTrigger[] viewTriggers;

    private StoryState[] storyStates;

    private Map<Integer, StoryState> storyStateIndex = new HashMap<>();

    private String filename;

    @Override public void initializeStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story) {
        StoryState initialState = storyStates[story.getStoryPosition()];
        processPreSubmitCalls(storyFunctionRepo, coven, person, story, initialState);

    }

    private void processPreSubmitCalls(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story,
        StoryState currentState) {
        StoryCall[] preSubmitCalls = currentState.getPreSubmitCalls();
        if (preSubmitCalls != null) {
            for (StoryCall preSubmitCall : preSubmitCalls) {
                preSubmitCall.call(storyFunctionRepo, coven, person, story);
            }
        }
    }

    @Override
    public void advanceStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story, StoryChoice choice) {
        if (story.getActionId() != choice.getActionId()) {
            log.error("Non-matching action ID, expected storyActionId={} but got choiceActionId={}", story.getActionId(),
                choice.getActionId());
            story.addFlash("Non-matching story ID when making a choice for " + this.getActionName());
            return;
        }
        int oldStateIdx = story.getStoryPosition();
        if (oldStateIdx < 0 || oldStateIdx >= storyStates.length) {
            log.error("Old state index out of bounds, expected 0 to maxStateIdx=" + (storyStates.length - 1) + " but got oldStateIdx="
                + oldStateIdx);
            story.setStoryPosition(0);
            return;
        }
        StoryState oldState = storyStates[oldStateIdx];
        int maxChoiceId = -1;
        if (oldState.getOptions() != null) {
            maxChoiceId = oldState.getOptions().length;
        }
        int requestedChoiceId = choice.getChoiceId();
        if (requestedChoiceId < 0 && (maxChoiceId < 0 || oldState.getCanCancel())) {
            log.info("Deleting story due to request to cancel/end: " + story);
            deleteStory(coven, person, story);
            return;
        }

        if (requestedChoiceId < 0 && maxChoiceId <= 0) {
            log.info("Deleting story due to request to cancel/end at dead end: " + story);
            deleteStory(coven, person, story);
            return;
        }

        if (requestedChoiceId < 0 || requestedChoiceId >= maxChoiceId) {
            log.error("Requested choiceId={} out of bounds in '{}' story, expected 0 to maxChoiceId={}", requestedChoiceId, getActionName(),
                maxChoiceId - 1);
            story.addFlash("Invalid choice ID in story " + this.getActionName() + ": " + requestedChoiceId);
            return;
        }
        StoryOption chosenOption = oldState.getOptions()[requestedChoiceId];
        if (!chosenOption.calculateIsEnabled(coven, person, story) || !chosenOption.calculateIsViewable(coven, person, story)) {
            log.error("Requested choiceId={} ({})not enabled/viewable in '{}' story", requestedChoiceId, chosenOption.getHeading(),
                getActionName());
            story.addFlash(
                String.format("Invalid choice ID in story %s: %d (%s). This option is not enabled/viewable!", this.getActionName(),
                    requestedChoiceId, chosenOption.getHeading()));
            return;
        }
        Map<String, String> formValues = choice.getFormValues();
        if (!validateFormValues(formValues, chosenOption.getFields(), coven, person, story)) {
            return;
        }
        if (formValues != null && formValues.size() > 0) {
            story.getState().putAll(formValues);
        }
        if (chosenOption.getTimeCost() > 0) {
            Daos.varRepo.processStatAdd("pi_time-" + chosenOption.getTimeCost(), coven, person, story);
        }
        processStatAdds(chosenOption.getStatAdds(), coven, person, story);
        StoryCall[] postSubmitCalls = chosenOption.getPostSubmitCalls();
        if (postSubmitCalls != null) {
            for (StoryCall storyCall : postSubmitCalls) {
                storyCall.call(storyFunctionRepo, coven, person, story);
            }
        }
        if (chosenOption.getRandomAdjustment() != null) {
            chosenOption.getRandomAdjustment().applyAdjustment(coven, person, story);
        }
        if (chosenOption.getTransitions() == null || chosenOption.getTransitions().length == 0) {
            // No transition for this option, must be an end state
            log.info("Deleting story due to null state transition: " + story);
            deleteStory(coven, person, story);
        } else {
            for (StateTransition stateTransition : chosenOption.getTransitions()) {
                if (stateTransition.isTriggered(coven, person, story.getState())) {
                    if (stateTransition.getTarget() < 0) {
                        log.info("Deleting story due to state transition to negative state: " + story);
                        deleteStory(coven, person, story);
                    } else {
                        StoryState newState = storyStates[stateTransition.getTarget()];
                        processStatAdds(newState.getStatAdds(), coven, person, story);
                        processPreSubmitCalls(storyFunctionRepo, coven, person, story, newState);
                    }
                    story.setStoryPosition(stateTransition.getTarget());

                    break;
                }
            }
        }
    }

    private void processStatAdds(String[] adds, Coven coven, Person person, StoryInstance story) {
        if (adds == null)
            return;
        for (String add : adds) {
            Daos.varRepo.processStatAdd(add, coven, person, story);
        }
    }

    private void deleteStory(Coven coven, Person person, StoryInstance story) {
        Daos.storyDao.deleteStory(story);
        story.setDeleted(true);
        if (person != null) {
            person.setActiveStoryId(-1);
        } else {
            coven.setActiveStoryId(-1);
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

    private static class SortByEnabled implements Comparator<StoryOption> {

        private Coven coven;

        private Person person;

        private StoryInstance story;

        private SortByEnabled(Coven coven, Person person, StoryInstance story) {
            this.coven = coven;
            this.person = person;
            this.story = story;
        }

        @Override public int compare(StoryOption o1, StoryOption o2) {
            return Boolean.valueOf(o2.isEnabled(coven, person, story)).compareTo(o1.isEnabled(coven, person, story));
        }

    }

    @Override public void generateStoryTextAndOptions(StoryView view, StoryInstance instance, Coven coven, Person person) {
        StoryState curState = storyStateIndex.get(instance.getStoryPosition());

        view.setHeading(curState.getHeading());
        view.setStoryText(curState.getText());
        view.setOptions(curState.getOptions());
        if (view.getOptions() != null) {
            Arrays.sort(view.getOptions(), new SortByEnabled(coven, person, instance));
        }
        view.setFlash(instance.getFlash());
        view.setValueChanges(instance.getValueChanges());
        view.setCanCancel(curState.getCanCancel());

        view.interpolateVariables(coven, person, instance);
    }

    @Override public boolean canStartStory(Coven coven, Person person) {
        boolean viewable = false;

        if (viewTriggers != null) {
            for (StoryTrigger viewTrigger : viewTriggers) {
                if (viewTrigger.isTriggered(coven, person, null)) {
                    viewable = true;
                    break;
                }
            }
        } else {
            viewable = true;
        }
        if (!viewable)
            return false;

        boolean enabled = false;
        if (enableTriggers == null)
            return true;
        for (StoryTrigger storyTrigger : enableTriggers) {
            if (storyTrigger.isTriggered(coven, person, null)) {
                enabled = true;
                break;
            }
        }
        return enabled;
    }

    @JsonProperty("states")
    public StoryState[] getStoryStates() {
        return storyStates;
    }

    @JsonProperty("states")
    public void setStoryStates(StoryState[] storyStates) {
        this.storyStates = storyStates;
        storyStateIndex.clear();
        int i = 0;
        for (StoryState storyState : storyStates) {
            storyState.setId(i++);
            storyStateIndex.put(storyState.getId(), storyState);
        }
    }

    public void validate() {
        try {
            // Verify that each story state is valid
            if (storyStates != null) {
                for (StoryState storyState : storyStates) {
                    storyState.validate();
                    storyStateOptionTransitionsAreValid(storyState);
                }
            }
            // validate story triggers
            if (enableTriggers != null) {
                for (StoryTrigger storyTrigger : enableTriggers) {
                    storyTrigger.validate();
                }
            }
            if (viewTriggers != null) {
                for (StoryTrigger viewTrigger : viewTriggers) {
                    viewTrigger.validate();
                }

            }
        } catch (Exception e) {
            throw new ValidationException("Error validating " + this, e);
        }
    }

    private void storyStateOptionTransitionsAreValid(StoryState storyState) {
        if (storyState.getOptions() != null) {
            for (StoryOption storyOption : storyState.getOptions()) {
                storyOption.validate();
                if (storyOption.getTransitions() != null) {
                    for (StateTransition stateTransition : storyOption.getTransitions()) {
                        if (stateTransition.getTarget() >= storyStates.length) {
                            throw new ValidationException("Invalid action: " + this.getFilename()
                                + " has a stateTransition=" + stateTransition
                                + " with a state target >= the total number of states=" + storyStates.length + ".");
                        }
                    }
                }
            }

        }
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public StoryTrigger[] getEnableTriggers() {
        return enableTriggers;
    }

    public void setEnableTriggers(StoryTrigger[] enableTriggers) {
        this.enableTriggers = enableTriggers;
    }

    public StoryTrigger[] getViewTriggers() {
        return viewTriggers;
    }

    public void setViewTriggers(StoryTrigger[] viewTriggers) {
        this.viewTriggers = viewTriggers;
    }

    @Override public String toString() {
        return "JsonStory{" +
            "filename='" + filename + '\'' +
            '}';
    }
}
