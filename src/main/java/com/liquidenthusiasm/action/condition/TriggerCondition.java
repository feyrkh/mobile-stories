package com.liquidenthusiasm.action.condition;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liquidenthusiasm.action.vars.VarDescription;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

@JsonSerialize(using = TriggerCondition.Serializer.class)
public class TriggerCondition {

    private static final Logger log = LoggerFactory.getLogger(TriggerCondition.class);

    private String variable;

    private TriggerConditionOperator op;

    private int intValue;

    private String strValue;

    public static final int VAR_TYPE = 1;

    public static final int VAR_SCOPE = 0;

    public boolean isTriggered(Coven coven, Person person, StoryInstance story) {
        return isTriggered(coven, person, story.getState());
    }

    public boolean isTriggered(Coven coven, Person person, Map<String, Object> storyState) {
        char varType = variable.charAt(VAR_TYPE);
        switch (varType) {
        case 'i':
            return isTriggeredInt(coven, person, storyState);
        case 's':
            return isTriggeredStr(coven, person, storyState);
        default:
            throw new RuntimeException("Unexpected variable type in " + variable + ": " + varType);
        }
    }

    private boolean isTriggeredInt(Coven coven, Person person, Map<String, Object> storyState) {

        int variableVal = Daos.varRepo.getIntProperty(variable, coven, person, storyState);

        int intValue = this.intValue;
        if (strValue != null) {
            intValue = Integer.parseInt(Daos.varRepo.interpolate(strValue, coven, person, storyState));
        }
        return isTriggeredInt(variableVal, intValue);
    }

    private boolean isTriggeredInt(long variableVal, int intValue) {
        switch (op) {
        case LT:
            return variableVal < intValue;
        case LTE:
            return variableVal <= intValue;
        case GT:
            return variableVal > intValue;
        case GTE:
            return variableVal >= intValue;
        case EQ:
            return variableVal == intValue;
        case NE:
            return variableVal != intValue;
        }
        throw new RuntimeException("Unexpected trigger condition operator: " + this);
    }

    private boolean isTriggeredStr(Coven coven, Person person, Map<String, Object> storyState) {

        String variableVal = Daos.varRepo.getStrProperty(variable, coven, person, storyState);
        String strValue = Daos.varRepo.interpolate(this.strValue, coven, person, storyState);

        return isTriggeredStr(variableVal, strValue);
    }

    private boolean isTriggeredStr(String variableVal, String strValue) {
        if ("null".equals(variableVal)) {
            variableVal = null;
        }
        switch (op) {
        case EQ:
            return Objects.equals(variableVal, strValue);
        case NE:
            return !Objects.equals(variableVal, strValue);
        }
        throw new RuntimeException("Unexpected trigger condition operator: " + this);
    }

    @Override public String toString() {
        return variable
            + op.toString()
            + String.valueOf(isIntegerVar() ? intValue : strValue);
    }

    @JsonCreator
    public static TriggerCondition parse(String value) {
        value = value.trim();
        TriggerCondition retval = new TriggerCondition();
        String[] pieces;
        pieces = value.split("<=|>=|!=|<|>|=", 2);
        pieces[0] = pieces[0].trim();
        retval.variable = pieces[0];
        pieces[1] = pieces[1].trim();
        if (retval.isIntegerVar()) {
            retval.intValue = Integer.parseInt(pieces[1]);
        } else {
            if ("null".equals(pieces[1]))
                retval.strValue = null;
            else
                retval.strValue = pieces[1];
        }
        String opName = value.substring(pieces[0].length(), value.trim().lastIndexOf(pieces[1])).trim();
        TriggerConditionOperator op;
        switch (opName) {
        case ">=":
            op = TriggerConditionOperator.GTE;
            break;
        case "<=":
            op = TriggerConditionOperator.LTE;
            break;
        case "=":
            op = TriggerConditionOperator.EQ;
            break;
        case "<":
            op = TriggerConditionOperator.LT;
            break;
        case ">":
            op = TriggerConditionOperator.GT;
            break;
        case "!=":
            op = TriggerConditionOperator.NE;
            break;
        default:
            throw new RuntimeException("Unepected operator in trigger condition for " + value + ": " + opName);
        }
        retval.op = op;
        return retval;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public TriggerConditionOperator getOp() {
        return op;
    }

    public void setOp(TriggerConditionOperator op) {
        this.op = op;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    private boolean isIntegerVar() {
        return variable.charAt(VAR_TYPE) == 'i';
    }

    @JsonIgnore
    public Object getValue() {
        if (isIntegerVar())
            return intValue;
        return strValue;
    }

    public void setStrValue(String strValue) {
        if ("null".equals(strValue))
            this.strValue = null;
        else
            this.strValue = strValue;
    }

    public void validate() {
        if ("null".equals(strValue))
            this.strValue = null;
        Daos.varRepo.validate(getVariable());
    }

    public static class Serializer extends JsonSerializer<TriggerCondition> {

        @Override
        public void serialize(TriggerCondition tc, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
            jsonGenerator.writeString(
                String.format("%s%s%s", tc.getVariable(), tc.getOp(), tc.isIntegerVar() ? tc.getIntValue() : tc.getStrValue()));
        }
    }

    @JsonIgnore
    public String getImage(Coven coven, Person person, StoryInstance state) {
        VarDescription varDesc = Daos.varRepo.getVar(variable);
        if (varDesc == null) {
            return VarDescription.ASSETS_ICON_DEFAULT_PNG;
        }
        return varDesc.getImage();
    }

    @JsonIgnore
    public String getDescription(Coven coven, Person person, StoryInstance state) {
        String relativeAmountDescription = op.toString();
        Object currentAmount = Daos.varRepo.getProperty(variable, coven, person, state.getState());

        switch (op) {
        case LT:
            relativeAmountDescription = "less than";
            break;
        case LTE:
            relativeAmountDescription = "no more than";
            break;
        case GT:
            relativeAmountDescription = "more than";
            break;
        case GTE:
            relativeAmountDescription = "at least";
            break;
        case EQ:
            relativeAmountDescription = "exactly";
            break;
        case NE:
            relativeAmountDescription = "anything but";
            break;
        }

        if (currentAmount == null)
            currentAmount = "none";

        VarDescription varDesc = Daos.varRepo.getVar(variable);
        Object comparisonValue = this.getValue();
        if (comparisonValue != null && comparisonValue instanceof String) {
            comparisonValue = Daos.varRepo.interpolate((String) comparisonValue, coven, person, state);
        }

        return String.format("%s must be %s \"%s\". You have \"%s\"", varDesc.getLabel(), relativeAmountDescription,
            comparisonValue == null ? "none" : varDesc.getValueDesc(String.valueOf(comparisonValue)),
            varDesc.getValueDesc(String.valueOf(currentAmount)));
    }
}
