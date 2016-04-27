package com.liquidenthusiasm.action.story;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryChoice {

    private static final Logger log = LoggerFactory.getLogger(StoryChoice.class);

    private Map<String, String> formValues;

    public StoryChoice() {
    }

    public StoryChoice(int choiceId) {
        this.choiceId = choiceId;
    }

    private int choiceId;

    public int getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public StoryChoice formValue(String name, String value) {
        if (formValues == null) {
            formValues = new HashMap<>();
        }
        formValues.put(name, value);
        return this;
    }

    public String formValue(String name) {
        if (formValues == null)
            return null;
        return formValues.get(name);
    }

    public Map<String, String> getFormValues() {
        return formValues;
    }

    public void setFormValues(Map<String, String> formValues) {
        this.formValues = formValues;
    }
}
