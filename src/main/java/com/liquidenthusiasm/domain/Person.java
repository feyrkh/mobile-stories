package com.liquidenthusiasm.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.dao.Daos;

public class Person {

    private String name = "Unnamed Individual";

    private long id;

    private long covenId;

    private long activeStoryId;

    private Long activeActionCategoryId;

    private int timeRemaining = 24;

    @JsonIgnore
    public void setIntProperty(String propName, int propVal) {
        switch (propName) {
        case "pi_time":
            timeRemaining = propVal;
            return;
        }
        if (propVal == 0) {
            Daos.propertyDao.deleteIntProperty(id, 0, propName);
        } else {
            Daos.propertyDao.updateIntProperty(id, 0, propName, propVal);
        }
    }

    @JsonIgnore
    public Integer getIntProperty(String propName) {
        switch (propName) {
        case "pi_time":
            return timeRemaining;
        }
        return Daos.propertyDao.getIntProperty(id, 0, propName);
    }

    @JsonIgnore
    public void setStrProperty(String propName, String propVal) {
        switch (propName) {
        case "ps_name":
            name = propVal;
            break;
        case "ps_location":
            setActiveActionCategoryId(ActionCategory.valueOf(propVal).getId());
            break;
        default:
            if (StringUtils.isEmpty(propVal)) {
                Daos.propertyDao.deleteStrProperty(id, 0, propName);
            } else {
                Daos.propertyDao.updateStrProperty(id, 0, propName, propVal);
            }
        }
    }

    @JsonIgnore
    public String getStrProperty(String propName) {
        switch (propName) {
        case "ps_name":
            return name;
        case "ps_location":
            return this.getCurrentCategory().name();
        }
        return Daos.propertyDao.getStrProperty(id, 0, propName);
    }

    public StoryInstance getRunningStory(long actionId) {
        return Daos.storyDao.findRunningStory(covenId, id, actionId);
    }

    public void setActiveStoryId(long activeStoryId) {
        this.activeStoryId = activeStoryId;
    }

    public long getActiveStoryId() {
        return activeStoryId;
    }

    public void save() {
        Daos.personDao.update(this);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setCovenId(long covenId) {
        this.covenId = covenId;
    }

    public long getCovenId() {
        return covenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getActiveActionCategoryId() {
        return activeActionCategoryId;
    }

    public void setActiveActionCategoryId(Long activeActionCategoryId) {
        this.activeActionCategoryId = activeActionCategoryId;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public static class Mapper implements ResultSetMapper<Person> {

        @Override public Person map(int i, ResultSet r, StatementContext statementContext) throws SQLException {
            Person s = new Person();
            s.setId(r.getInt("id"));
            s.setCovenId(r.getLong("covenId"));
            s.setActiveStoryId(r.getLong("activeStoryId"));
            s.setActiveActionCategoryId(r.getLong("activeActionCategoryId"));
            s.setName(r.getString("name"));
            s.setTimeRemaining(r.getInt("timeRemaining"));
            return s;
        }
    }

    @JsonIgnore
    public ActionCategory getCurrentCategory() {
        return ActionCategory.getById(getActiveActionCategoryId());
    }

    @JsonIgnore
    public ActionCategory getParentCategory() {
        return ActionCategory.getParent(getCurrentCategory());
    }

    @JsonIgnore
    public Collection<ActionCategory> getChildCategories() {
        return ActionCategory.getChildren(getCurrentCategory());
    }

    @JsonIgnore
    public Collection<ActionCategory> getSiblingCategories() {
        return ActionCategory.getSiblings(getCurrentCategory());
    }

}
