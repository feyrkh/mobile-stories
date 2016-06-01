package com.liquidenthusiasm.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.vars.VarDescription;
import com.liquidenthusiasm.action.vars.VarRepo;
import com.liquidenthusiasm.dao.Daos;

public class ValueChange {

    private static final Logger log = LoggerFactory.getLogger(ValueChange.class);

    private String varTrueName;

    private String var;

    private String changeAmount;

    private String oldValue;

    private String newValue;

    private boolean adminOnly = false;

    public ValueChange() {
    }

    public ValueChange(String var, int changeAmount, int oldValue, int newValue) {
        setVar(var);
        this.changeAmount = String.valueOf(changeAmount);
        VarDescription varDesc = Daos.varRepo.getVar(varTrueName);
        if (varDesc == null) {
            this.oldValue = String.valueOf(oldValue);
            this.newValue = String.valueOf(newValue);
        } else {
            this.newValue = varDesc.getValueDesc(String.valueOf(newValue));
            this.oldValue = varDesc.getValueDesc(String.valueOf(oldValue));
        }
    }

    public ValueChange(String var, String oldValue, String newValue) {
        setVar(var);
        this.changeAmount = null;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public boolean getAdminOnly() {
        return adminOnly;
    }

    public String getVar() {
        return var;
    }

    public final void setVar(String var) {
        this.varTrueName = var;
        VarDescription varDesc = Daos.varRepo.getVar(varTrueName);
        if (varDesc != null) {
            this.var = varDesc.getLabel();
            this.adminOnly = VarRepo.getPropertyScope(varTrueName) == 's' || varDesc.getHideChanges();
        } else {
            this.var = var;
            this.adminOnly = false;
        }
    }

    public String getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(String changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public static ValueChange incCovenVar(Coven coven, String intVarName, int incrementBy) {
        Integer oldValue = coven.getIntProperty(intVarName);
        if (oldValue == null)
            oldValue = 0;
        int newValue = oldValue + incrementBy;
        VarDescription desc = Daos.varRepo.getVar(intVarName);
        if (desc.getMin() > newValue)
            newValue = desc.getMin();
        if (desc.getMax() != 0 && desc.getMax() < newValue)
            newValue = desc.getMax();
        coven.setIntProperty(intVarName, newValue);
        return new ValueChange(intVarName, incrementBy, oldValue, newValue);
    }

    public static ValueChange incPersonVar(Person person, String intVarName, int incrementBy) {
        Integer oldValue = person.getIntProperty(intVarName);
        if (oldValue == null)
            oldValue = 0;
        int newValue = oldValue + incrementBy;
        VarDescription desc = Daos.varRepo.getVar(intVarName);
        if (desc.getMin() > newValue)
            newValue = desc.getMin();
        if (desc.getMax() != 0 && desc.getMax() < newValue)
            newValue = desc.getMax();
        person.setIntProperty(intVarName, newValue);
        return new ValueChange(intVarName, incrementBy, oldValue, newValue);
    }

    public static ValueChange incStoryVar(StoryInstance story, String intVarName, int incrementBy) {
        Integer oldValue = story.getIntProperty(intVarName);
        if (oldValue == null)
            oldValue = 0;

        int newValue = oldValue + incrementBy;
        VarDescription desc = Daos.varRepo.getVar(intVarName);
        if (desc.getMin() > newValue)
            newValue = desc.getMin();
        if (desc.getMax() != 0 && desc.getMax() < newValue)
            newValue = desc.getMax();
        story.setIntProperty(intVarName, newValue);
        return new ValueChange(intVarName, incrementBy, oldValue, newValue);
    }

    public String getVarTrueName() {
        return varTrueName;
    }

    public void setVarTrueName(String varTrueName) {
        this.varTrueName = varTrueName;
    }

    public static ValueChange setCovenVar(Coven coven, String intVarName, int amt) {
        Integer oldValue = coven.getIntProperty(intVarName);
        if (oldValue == null)
            oldValue = 0;

        VarDescription desc = Daos.varRepo.getVar(intVarName);
        if (desc.getMin() > amt)
            amt = desc.getMin();
        if (desc.getMax() != 0 && desc.getMax() < amt)
            amt = desc.getMax();
        coven.setIntProperty(intVarName, amt);
        return new ValueChange(intVarName, amt - oldValue, oldValue, amt);
    }

    public static ValueChange setPersonVar(Person p, String intVarName, int amt) {
        Integer oldValue = p.getIntProperty(intVarName);
        if (oldValue == null)
            oldValue = 0;

        VarDescription desc = Daos.varRepo.getVar(intVarName);
        if (desc.getMin() > amt)
            amt = desc.getMin();
        if (desc.getMax() != 0 && desc.getMax() < amt)
            amt = desc.getMax();
        p.setIntProperty(intVarName, amt);
        return new ValueChange(intVarName, amt - oldValue, oldValue, amt);
    }

    public static ValueChange setStoryVar(StoryInstance story, String intVarName, int amt) {
        Integer oldValue = story.getIntProperty(intVarName);
        if (oldValue == null)
            oldValue = 0;

        VarDescription desc = Daos.varRepo.getVar(intVarName);
        if (desc.getMin() > amt)
            amt = desc.getMin();
        if (desc.getMax() != 0 && desc.getMax() < amt)
            amt = desc.getMax();
        story.setIntProperty(intVarName, amt);
        return new ValueChange(intVarName, amt - oldValue, oldValue, amt);
    }

    public static ValueChange setCovenVar(Coven coven, String strVarName, String newVal) {
        String oldValue = coven.getStrProperty(strVarName);
        coven.setStrProperty(strVarName, newVal);
        return new ValueChange(strVarName, oldValue, newVal);
    }

    public static ValueChange setPersonVar(Person p, String strVarName, String newVal) {
        String oldValue = p.getStrProperty(strVarName);
        p.setStrProperty(strVarName, newVal);
        return new ValueChange(strVarName, oldValue, newVal);
    }

    public static ValueChange setStoryVar(StoryInstance story, String strVarName, String newVal) {
        String oldValue = story.getStrProperty(strVarName);
        story.setStrProperty(strVarName, newVal);
        return new ValueChange(strVarName, oldValue, newVal);
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public void setAdminOnly(boolean adminOnly) {
        this.adminOnly = adminOnly;
    }
}
