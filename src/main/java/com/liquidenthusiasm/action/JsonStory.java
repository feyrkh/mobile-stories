package com.liquidenthusiasm.action;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.liquidenthusiasm.action.condition.StoryTrigger;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.action.story.StoryState;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

public class JsonStory extends AbstractAction {

    private static final Logger log = LoggerFactory.getLogger(JsonStory.class);

    private StoryTrigger[] storyTriggers;

    private StoryState[] storyStates;

    private Map<Integer, StoryState> storyStateIndex = new HashMap<>();

    @Override public void initializeStory(StoryInstance story) {

    }

    @Override public void advanceStory(Coven coven, StoryInstance story, StoryChoice choice) {

    }

    @Override public void generateStoryTextAndOptions(StoryView view, StoryInstance instance) {
        StoryState curState = storyStateIndex.get(instance.getStoryPosition());
        view.setStoryText(curState.getText());
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
