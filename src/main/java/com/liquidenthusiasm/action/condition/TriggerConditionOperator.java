package com.liquidenthusiasm.action.condition;

public enum TriggerConditionOperator {
    LT("<"), LTE("<="), GT(">"), GTE(">="), EQ("="), NE("!=");

    private final String stringRep;

    TriggerConditionOperator(String stringRep) {

        this.stringRep = stringRep;
    }

    @Override public String toString() {
        return stringRep;
    }
}