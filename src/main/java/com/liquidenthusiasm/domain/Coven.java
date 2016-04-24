package com.liquidenthusiasm.domain;

import java.io.Serializable;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.validator.constraints.Length;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquidenthusiasm.dao.CovenDao;

public class Coven implements Principal, Sanitizable, Serializable {

    public static final String INT_PROP_LIVING_SPACE = "Coven:Living Space";

    public static final String INT_PROP_MEMBERS = "Coven:Members";

    public static CovenDao covenDao;

    private long id;

    @Length(max = 63)
    private String displayName;

    private String password;

    private String name;

    private boolean admin;

    private long activeStoryId = -1;

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
            covenDao.deleteIntProperty(id, propName);
        } else {
            covenDao.updateIntProperty(id, propName, propVal);
        }
    }

    @JsonIgnore
    public int getIntProperty(String propName) {
        return covenDao.getIntProperty(id, propName);
    }

    public StoryInstance getRunningStory(long actionId) {
        return covenDao.findRunningStory(getId(), actionId);
    }

    public void saveStory(StoryInstance storyInstance) {
        covenDao.saveRunningStory(storyInstance);
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
}
