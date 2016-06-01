package com.liquidenthusiasm.domain;

import java.io.Serializable;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.dao.Daos;

public class Coven implements Principal, Sanitizable, Serializable {

    public static final String INT_PROP_LIVING_SPACE = "Coven:Living Space";

    public static final String INT_PROP_MEMBERS = "Coven:Members";

    public static final int NO_FOCUSED_PERSON = 0;

    private long id;

    @Length(max = 63)
    private String displayName;

    private String password;

    private String name;

    private boolean admin;

    private long activeStoryId = -1;

    private long focusedPersonId = NO_FOCUSED_PERSON;

    @JsonIgnore
    private ImmutableList<Person> members;

    public Coven() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override public String toString() {
        return "Coven{" +
            "id=" + id +
            ", displayName='" + displayName + '\'' +
            ", name='" + name + '\'' +
            ", admin=" + admin +
            '}';
    }

    public void sanitize() {
        setPassword(null);
    }

    @JsonIgnore
    public void setIntProperty(String propName, int propVal) {
        if (propVal == 0) {
            Daos.propertyDao.deleteIntProperty(id, 0, propName);
        } else {
            Daos.propertyDao.updateIntProperty(id, 0, propName, propVal);
        }
    }

    @JsonIgnore
    public Integer getIntProperty(String propName) {
        switch (propName) {
        case "ci_admin":
            return isAdmin() ? 1 : 0;
        }
        return Daos.propertyDao.getIntProperty(id, 0, propName);
    }

    @JsonIgnore
    public void setStrProperty(String propName, String propVal) {
        if (StringUtils.isEmpty(propVal)) {
            Daos.propertyDao.deleteStrProperty(id, 0, propName);
        } else {
            Daos.propertyDao.updateStrProperty(id, 0, propName, propVal);
        }
    }

    @JsonIgnore
    public String getStrProperty(String propName) {
        return Daos.propertyDao.getStrProperty(id, 0, propName);
    }

    public StoryInstance getRunningStory(long actionId) {
        return Daos.storyDao.findRunningStory(getId(), focusedPersonId, actionId);
    }

    public long getFocusedPersonId() {
        return focusedPersonId;
    }

    public void save() {
        Daos.covenDao.update(this);
    }

    @JsonIgnore
    public Person getFocusedPerson() {
        return Daos.personDao.findById(focusedPersonId);
    }

    public static class Mapper implements ResultSetMapper<Coven> {

        @Override public Coven map(int i, ResultSet r, StatementContext ctx) throws SQLException {
            Coven c = new Coven();
            c.setId(r.getLong("id"));
            c.setDisplayName(r.getString("displayName"));
            c.setName(r.getString("name"));
            c.setPassword(r.getString("password"));
            c.setAdmin(r.getBoolean("admin"));
            c.setActiveStoryId(r.getLong("activeStoryId"));
            c.setFocusedPersonId(r.getLong("focusedPersonId"));
            return c;
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override public String getName() {
        return name;
    }

    public long getActiveStoryId() {
        return activeStoryId;
    }

    public void setActiveStoryId(long activeStoryId) {
        this.activeStoryId = activeStoryId;
    }

    public void setFocusedPersonId(long focusedPersonId) {
        this.focusedPersonId = focusedPersonId;
    }

    public void addMember(Person member) {
        members = null;
    }

    @JsonIgnore
    public ImmutableList<Person> getMembers() {
        members = Daos.personDao.listByCovenId(getId());
        return members;
    }
}
