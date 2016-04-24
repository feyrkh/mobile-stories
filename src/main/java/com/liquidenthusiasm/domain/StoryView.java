package com.liquidenthusiasm.domain;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.story.StoryOption;

/**
 * View of a StoryInstance for the frontend user.
 */
public class StoryView {

    private static final Logger log = LoggerFactory.getLogger(StoryView.class);

    private long id;

    private long covenId;

    private long actionId;

    private long personId;

    private String storyName;

    private String storyText;

    private List<StoryOption> options = new ArrayList<>();

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCovenId() {
        return covenId;
    }

    public void setCovenId(long covenId) {
        this.covenId = covenId;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public String getStoryText() {
        return storyText;
    }

    public void setStoryText(String storyText) {
        this.storyText = storyText;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public List<StoryOption> getOptions() {
        return options;
    }

    public void setOptions(List<StoryOption> options) {
        this.options = options;
    }
}
