package com.liquidenthusiasm.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StoryInstance {

    private static final Logger log = LoggerFactory.getLogger(StoryInstance.class);

    static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String VALUE_CHANGE_KEY = "!$VC$!";

    private long covenId;

    private long personId;

    private long actionId;

    private int storyPosition;

    private Map<String, Object> state = new HashMap<>();

    private String error;

    private String flash;

    private boolean deleted;

    @JsonProperty("state")
    public String getStateJson() {
        try {
            return MAPPER.writeValueAsString(state);
        } catch (Exception e) {
            log.error("Exception while writing state for " + this, e);
            return "{}";
        }
    }

    @JsonProperty("state")
    public void setStateJson(String json) {
        try {
            state = MAPPER.readValue(json, HashMap.class);
        } catch (Exception e) {
            log.error("Exception while reading state for " + this, e);
            state = new HashMap<>();
        }
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public long getActionId() {
        return actionId;
    }

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public void addFlash(String msg) {
        msg = StringUtils.capitalize(msg);
        if (flash == null) {
            flash = msg;
        } else {
            flash = String.format("%s\n%s", flash, msg);
        }
    }

    public void cleanBeforeNewChoice() {
        setFlash(null);
        clearValueChanges();
    }

    public Integer getIntProperty(String intVarName) {
        Object stateVal = state(intVarName);
        if (stateVal == null) {
            return 0;
        }
        return (Integer) stateVal;
    }

    public void setIntProperty(String intVarName, int newValue) {
        state(intVarName, newValue);
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setStrProperty(String strVarName, String newValue) {
        if (newValue != null) {
            state(strVarName, newValue);
        } else {
            state.remove(strVarName);
        }
    }

    public String getStrProperty(String strVarName) {
        return String.valueOf(state(strVarName));
    }

    public static class Mapper implements ResultSetMapper<StoryInstance> {

        @Override public StoryInstance map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
            StoryInstance s = new StoryInstance();
            s.setStoryPosition(r.getInt("storyPosition"));
            s.setActionId(r.getLong("actionId"));
            s.setCovenId(r.getLong("covenId"));
            s.setPersonId(r.getLong("personId"));
            s.setStateJson(r.getString("stateJson"));
            return s;
        }
    }

    public long getCovenId() {
        return covenId;
    }

    public void setCovenId(long covenId) {
        this.covenId = covenId;
    }

    @JsonIgnore
    public Map<String, Object> getState() {
        return state;
    }

    @JsonIgnore
    public void setState(Map<String, Object> state) {
        this.state = state;
    }

    public long getPersonId() {
        return personId;
    }

    public void setPersonId(long personId) {
        this.personId = personId;
    }

    public int getStoryPosition() {
        return storyPosition;
    }

    public void setStoryPosition(int storyPosition) {
        this.storyPosition = storyPosition;
    }

    @JsonIgnore
    public List<ValueChange> getValueChanges() {
        if (state == null)
            return null;
        return (List<ValueChange>) state(VALUE_CHANGE_KEY);
    }

    private void clearValueChanges() {
        state.remove(VALUE_CHANGE_KEY);
    }

    public void valueChange(ValueChange vc) {
        List<ValueChange> vcArr = (List<ValueChange>) state(VALUE_CHANGE_KEY);
        if (vcArr == null) {
            vcArr = new ArrayList<>(1);
            state(VALUE_CHANGE_KEY, vcArr);
        }
        vcArr.add(vc);
    }

    public StoryInstance state(String key, Object value) {
        if (state == null) {
            state = new HashMap<>();
        }
        state.put(key, value);
        return this;
    }

    public Object state(String key) {
        if (state == null) {
            return null;
        }
        return state.get(key);
    }

}
