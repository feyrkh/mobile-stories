package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryOption {

    private static final Logger log = LoggerFactory.getLogger(StoryOption.class);

    private String heading;

    private String text;

    private FieldDef[] fields;

    private StoryCall[] postSubmitCalls;

    private StateTransition[] transitions;

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

    public StoryCall[] getPostSubmitCalls() {
        return postSubmitCalls;
    }

    public void setPostSubmitCalls(StoryCall[] postSubmitCalls) {
        this.postSubmitCalls = postSubmitCalls;
    }

    public StateTransition[] getTransitions() {
        return transitions;
    }

    public void setTransitions(StateTransition[] transitions) {
        this.transitions = transitions;
    }

    public FieldDef[] getFields() {
        return fields;
    }

    public void setFields(FieldDef[] fields) {
        this.fields = fields;
    }
}
