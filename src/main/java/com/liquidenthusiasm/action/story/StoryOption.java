package com.liquidenthusiasm.action.story;

import java.util.Objects;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.liquidenthusiasm.action.condition.StoryTrigger;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.Sanitizable;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.util.RandomUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoryOption implements Sanitizable {

    private static final Logger log = LoggerFactory.getLogger(StoryOption.class);

    private int id;

    private String heading;

    private String text;

    private FieldDef[] fields;

    private StoryCall[] postSubmitCalls;

    private StateTransition[] transitions;

    private StoryTrigger[] enableTriggers;

    private StoryTrigger[] viewTriggers;

    private String[] statAdds;

    private RandomAdjustment randomAdjustment;

    private Boolean isViewable;

    private Boolean isEnabled;

    private int timeCost = 0;

    public void sanitize() {
        this.postSubmitCalls = null;
        this.transitions = null;
    }

    @Override public String toString() {
        return "StoryOption{" +
            "heading='" + heading + '\'' +
            '}';
    }

    public void validate() {
        if (StringUtils.isEmpty(heading))
            throw new ValidationException("Heading must not be blank in StoryOption");

        try {
            if (timeCost < 0) {
                throw new ValidationException("timeCost=" + timeCost + " should be >= 0");
            }
            if (transitions != null) {
                for (StateTransition transition : transitions) {
                    transition.validate();
                }
            }
            if (transitions == null || transitions.length == 0) {
                throw new ValidationException("Every StoryOption must have at least 1 transition");
            }

            if (fields != null) {
                for (FieldDef field : fields) {
                    field.validate();
                }
            }

            if (postSubmitCalls != null) {
                for (StoryCall postSubmitCall : postSubmitCalls) {
                    postSubmitCall.validate();
                }
            }

            if (statAdds != null) {
                for (String statAdd : statAdds) {
                    String[] bits = statAdd.split("\\+|=|-", 2);
                    String varName = bits[0];
                    String val = bits[1];
                    if (!varName.startsWith("s")) {
                        if (Daos.varRepo.getVar(varName).isUndefined()) {
                            throw new ValidationException(
                                "Non-story-limited statAdds entries must be registered in the var repo: '" + statAdd + "'");
                        }
                    }
                    if (varName.charAt(1) == 'i' && !(val.startsWith("{{") && val.endsWith("}}"))) {
                        // Verify that the value is numeric
                        try {
                            Integer.parseInt(val);
                        } catch (NumberFormatException e) {
                            throw new ValidationException(
                                "integer statAdds entries must have an integer after the operator: '" + statAdd + "'", e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new ValidationException("Error validating " + this, e);
        }
    }

    public boolean isViewable(Coven coven, Person person, StoryInstance story) {
        if (isViewable == null)
            isViewable = calculateIsViewable(coven, person, story);
        return isViewable;
    }

    public boolean calculateIsViewable(Coven coven, Person person, StoryInstance story) {
        if (viewTriggers == null || viewTriggers.length == 0)
            return true;
        for (StoryTrigger viewTrigger : viewTriggers) {
            if (viewTrigger.isTriggered(coven, person, story.getState())) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnabled(Coven coven, Person person, StoryInstance story) {
        if (person.getTimeRemaining() < timeCost)
            return false;
        if (isEnabled == null)
            isEnabled = calculateIsEnabled(coven, person, story);
        return isEnabled;
    }

    public boolean calculateIsEnabled(Coven coven, Person person, StoryInstance story) {
        if (enableTriggers == null || enableTriggers.length == 0)
            return true;
        for (StoryTrigger enableTrigger : enableTriggers) {
            if (enableTrigger.isTriggered(coven, person, story.getState())) {
                return true;
            }
        }
        return false;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getText() {
        return text;
    }

    public String getHtmlText(Coven coven, Person person, StoryInstance story) {
        //        Daos.varRepo.getProperty(variable, coven, person, story.getState());
        if (text == null)
            return "";
        if (fields == null || fields.length == 0)
            return text;
        String html = text;
        for (FieldDef field : fields) {
            String fieldHtml = generateFieldHtml(field, coven, person, story);
            if (fieldHtml != null) {
                String fieldReplaceToken = "@" + field.getName();
                html = Strings.replace(html, fieldReplaceToken, fieldHtml);
            }
        }
        return Daos.varRepo.interpolate(html, coven, person, story);
    }

    private String generateFieldHtml(FieldDef field, Coven coven, Person person, StoryInstance story) {
        switch (field.getType()) {

        case text:
            return generateTextFieldHtml(field, coven, person, story);
        case select:
            return generateSelectFieldHtml(field, coven, person, story);
        default:
            log.error("Unexpected field type in {}", field);
            return null;
        }
    }

    private String generateSelectFieldHtml(FieldDef field, Coven coven, Person person, StoryInstance story) {
        StringBuilder sb = new StringBuilder("<select class='select field' name=\"").append(field.getName()).append("\">");
        if (field.getOptions() != null) {
            Object curValue = Daos.varRepo.getProperty(field.getName(), coven, person, story.getState());
            if (curValue == null) {
                if (field.getDefaultValue() == null) {
                    curValue = RandomUtil.randomChoice(field.getOptions()).getValue();
                } else {
                    curValue = field.getDefaultValue();
                }
            }

            for (FieldDefSelectOption option : field.getOptions()) {
                boolean selected = Objects.equals(option.getValue(), curValue);
                sb.append("\n<option value=\"").append(option.getValue()).append("\"");
                if (selected)
                    sb.append(" selected");
                sb.append(">").append(option.getLabel()).append("</option>");
            }
        }
        sb.append("</select>");
        return sb.toString();
    }

    private String generateTextFieldHtml(FieldDef field, Coven coven, Person person, StoryInstance story) {

        Object curValue = Daos.varRepo.getPropertyIfExists(field.getName(), coven, person, story.getState());
        if (curValue == null) {
            curValue = field.getDefaultValue();
        }
        return String
            .format("<input type='text' class='text field' name=\"%s\" value=\"%s\" %s/>", field.getName(), curValue,
                field.getWidth() == null ? "" : "size=" + field.getWidth());
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RandomAdjustment getRandomAdjustment() {
        return randomAdjustment;
    }

    public void setRandomAdjustment(RandomAdjustment randomAdjustment) {
        this.randomAdjustment = randomAdjustment;
    }

    public int getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(int timeCost) {
        this.timeCost = timeCost;
    }

    public String[] getStatAdds() {
        return statAdds;
    }

    public void setStatAdds(String[] statAdds) {
        this.statAdds = statAdds;
    }
}
