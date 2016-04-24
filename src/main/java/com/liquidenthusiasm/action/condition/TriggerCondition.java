package com.liquidenthusiasm.action.condition;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

public class TriggerCondition {

    private static final Logger log = LoggerFactory.getLogger(TriggerCondition.class);

    public static final int VAR_TYPE = 1;

    public static final int VAR_SCOPE = 0;

    private String variable;

    private TriggerConditionOperator op;

    private int intValue;

    private String strValue;

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

    private int getIntProperty(Coven coven, Person person, Map<String, Object> storyState) {
        switch (variable.charAt(VAR_SCOPE)) {
        case 'c':
            return coven.getIntProperty(variable);
        //case 'p': return person.getIntProperty(variable);
        case 's':
            return (int) storyState.get(variable);
        default:
            throw new RuntimeException("Unexpected variable scope in " + variable + ": " + variable.charAt(VAR_SCOPE));
        }
    }

    private String getStrProperty(Coven coven, Person person, Map<String, Object> storyState) {
        switch (variable.charAt(VAR_SCOPE)) {
        //        case 'c':
        //            return coven.getStrProperty(variable);
        //case 'p': return person.getIntProperty(variable);
        case 's':
            return String.valueOf(storyState.get(variable));
        default:
            throw new RuntimeException("Unexpected variable scope in " + variable + ": " + variable.charAt(VAR_SCOPE));
        }
    }

    private boolean isTriggeredInt(Coven coven, Person person, Map<String, Object> storyState) {

        int variableVal = getIntProperty(coven, person, storyState);

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

        String variableVal = getStrProperty(coven, person, storyState);

        switch (op) {
        case EQ:
            return Objects.equals(variableVal, strValue);
        case NE:
            return !Objects.equals(variableVal, strValue);
        }
        throw new RuntimeException("Unexpected trigger condition operator: " + this);
    }

    @Override public String toString() {
        return "'" + variable + '\'' +
            " " + op +
            " " + intValue;
    }

    public static TriggerCondition parse(String value) {
        value = value.trim();
        TriggerCondition retval = new TriggerCondition();
        String[] pieces;
        int varValLen = 0;
        pieces = value.split("<=|>=|!=|<|>|=", 2);
        pieces[0] = pieces[0].trim();
        retval.variable = pieces[0];
        varValLen += pieces[0].length();
        pieces[1] = pieces[1].trim();
        if (retval.variable.charAt(VAR_TYPE)    == 'i') {
            retval.intValue = Integer.parseInt(pieces[1]);
        } else {
            retval.strValue = pieces[1];
        }
        varValLen += pieces[1].length();
        String opName = value.substring(pieces[0].length(), value.trim().lastIndexOf(pieces[1])).trim();
        TriggerConditionOperator op = TriggerConditionOperator.EQ;
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

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }
}