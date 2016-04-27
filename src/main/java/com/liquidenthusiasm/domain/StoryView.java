package com.liquidenthusiasm.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquidenthusiasm.action.story.FieldDef;
import com.liquidenthusiasm.action.story.StoryOption;
import com.liquidenthusiasm.util.VariableLookup;

/**
 * View of a StoryInstance for the frontend user.
 */
public class StoryView {

    private static final Logger log = LoggerFactory.getLogger(StoryView.class);

    private static final ObjectMapper mapper = new ObjectMapper();


    private long id;

    private long covenId;

    private long actionId;

    private long personId;

    private String heading;

    private String storyText;

    private StoryOption[] options;

    private String flash;

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

    public String getHeading() {
        return heading;
    }

    public void setHeading(String storyName) {
        this.heading = storyName;
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

    public StoryOption[] getOptions() {
        return options;
    }

    public void setOptions(StoryOption[] options) {
        try {
            String content = mapper.writeValueAsString(options);
            this.options = mapper.readValue(content, StoryOption[].class);
        } catch (Exception e) {
            log.error("Failed to serialize/deserialize options, can't safely perform substitutions.", e);
            this.options = options;
        }
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public String getFlash() {
        return flash;
    }

    public void interpolateVariables(Coven coven, Person person, StoryInstance story) {
        if (options != null) {
            for (StoryOption option : options) {
                interpolate(option, coven, person, story);
            }
        }
        String flash = this.getFlash();
        if (flash != null && flash.contains("{{")) {
            this.setFlash(VariableLookup.interpolate(flash, coven, person, story));
        }
        String heading = this.getHeading();
        if (heading != null && heading.contains("{{")) {
            this.setHeading(VariableLookup.interpolate(heading, coven, person, story));
        }
        String storyText = this.getStoryText();
        if (storyText != null && storyText.contains("{{")) {
            this.setStoryText(VariableLookup.interpolate(storyText, coven, person, story));
        }
    }

    private void interpolate(StoryOption option, Coven coven, Person person, StoryInstance story) {
        String heading = option.getHeading();
        if (heading != null && heading.contains("{{")) {
            option.setHeading(VariableLookup.interpolate(heading, coven, person, story));
        }
        String text = option.getText();

        if (text != null && text.contains("{{")) {
            option.setText(VariableLookup.interpolate(text, coven, person, story));
        }

        FieldDef[] fields = option.getFields();
        if (fields != null) {
            for (FieldDef field : fields) {
                String defaultValue = field.getDefaultValue();
                if (defaultValue.contains("{{")) {
                    field.setDefaultValue(VariableLookup.interpolate(defaultValue, coven, person, story));
                }
            }
        }
    }
}
