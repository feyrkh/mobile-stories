package com.liquidenthusiasm.action.story;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryChoice {

    private static final Logger log = LoggerFactory.getLogger(StoryChoice.class);

    private int actionCost = 1;

    private List<FieldDef> fieldDefs;

    private Map<String, String> formValues;

    public StoryChoice() {
    }

    public StoryChoice(int choiceId, String choiceDescription) {
        this.choiceDescription = choiceDescription;
        this.choiceId = choiceId;
    }

    private String choiceDescription;

    private int choiceId;

    public String getChoiceDescription() {
        return choiceDescription;
    }

    public void setChoiceDescription(String choiceDescription) {
        this.choiceDescription = choiceDescription;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public StoryChoice actionCost(int cost) {
        actionCost = cost;
        return this;
    }

    public StoryChoice formValue(String name, String value) {
        if (formValues == null) {
            formValues = new HashMap<>();
        }
        formValues.put(name, value);
        return this;
    }

    public StoryChoice formDef(FieldDef fieldDef) {
        if (this.fieldDefs == null) {
            this.fieldDefs = new ArrayList<>();
        }
        this.fieldDefs.add(fieldDef);
        return this;
    }

    public String formValue(String name) {
        if (formValues == null)
            return null;
        return formValues.get(name);
    }

    public List<FieldDef> getFieldDefs() {
        return fieldDefs;
    }

    public void setFieldDefs(List<FieldDef> fieldDefs) {
        this.fieldDefs = fieldDefs;
    }

    public Map<String, String> getFormValues() {
        return formValues;
    }

    public void setFormValues(Map<String, String> formValues) {
        this.formValues = formValues;
    }
}
