package com.liquidenthusiasm.domain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquidenthusiasm.action.story.FieldDef;
import com.liquidenthusiasm.action.story.StoryOption;
import com.liquidenthusiasm.dao.Daos;

/**
 * View of a StoryInstance for the frontend user.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private List<ValueChange> valueChanges;

    private boolean canCancel;

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
        if (options == null || options.length == 0) {
            this.options = null;
            return;
        }
        try {
            String content = mapper.writeValueAsString(options);
            this.options = mapper.readValue(content, StoryOption[].class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize/deserialize options, can't safely perform substitutions.", e);
        }
        int id = 0;
        for (StoryOption option : this.options) {
            option.setId(id);
            option.sanitize();
            id++;
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
            this.setFlash(Daos.varRepo.interpolate(flash, coven, person, story));
        }
        String heading = this.getHeading();
        if (heading != null && heading.contains("{{")) {
            this.setHeading(Daos.varRepo.interpolate(heading, coven, person, story));
        }
        String storyText = this.getStoryText();
        if (storyText != null && storyText.contains("{{")) {
            this.setStoryText(Daos.varRepo.interpolate(storyText, coven, person, story));
        }
    }

    private void interpolate(StoryOption option, Coven coven, Person person, StoryInstance story) {
        String heading = option.getHeading();
        if (heading != null && heading.contains("{{")) {
            option.setHeading(Daos.varRepo.interpolate(heading, coven, person, story));
        }
        String text = option.getText();

        if (text != null && text.contains("{{")) {
            option.setText(Daos.varRepo.interpolate(text, coven, person, story));
        }

        FieldDef[] fields = option.getFields();
        if (fields != null) {
            for (FieldDef field : fields) {
                String defaultValue = field.getDefaultValue();
                if (defaultValue != null) {
                    if (defaultValue.contains("{{")) {
                        field.setDefaultValue(Daos.varRepo.interpolate(defaultValue, coven, person, story));
                    }
                }
            }
        }
    }

    public List<ValueChange> getValueChanges() {
        return valueChanges;
    }

    public void setValueChanges(List<ValueChange> valueChanges) {
        this.valueChanges = valueChanges;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean isCanCancel() {
        return canCancel || options == null || options.length == 0;
    }
}
