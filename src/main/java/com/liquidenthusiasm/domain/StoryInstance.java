package com.liquidenthusiasm.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    private long covenId;

    private long personId;

    private long actionId;

    private int storyPosition;

    private Map<String, Object> state = new HashMap<>();

    private String error;

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

    public StoryInstance state(String key, Object value) {
        state.put(key, value);
        return this;
    }

    public Object state(String key) {
        return state.get(key);
    }

}
