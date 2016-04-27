package com.liquidenthusiasm.domain;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.dao.PropertyDao;

public class Person {

    private String name;

    public static CovenDao covenDao;

    public static PropertyDao propertyDao;

    private long id;

    private long covenId;

    @JsonIgnore
    public void setIntProperty(String propName, int propVal) {
        if (propVal == 0) {
            propertyDao.deleteIntProperty(id, 0, propName);
        } else {
            propertyDao.updateIntProperty(id, 0, propName, propVal);
        }
    }

    @JsonIgnore
    public int getIntProperty(String propName) {
        return propertyDao.getIntProperty(id, 0, propName);
    }

    @JsonIgnore
    public void setStrProperty(String propName, String propVal) {
        if (StringUtils.isEmpty(propVal)) {
            propertyDao.deleteStrProperty(id, 0, propName);
        } else {
            propertyDao.updateStrProperty(id, 0, propName, propVal);
        }
    }

    @JsonIgnore
    public String getStrProperty(String propName) {
        return propertyDao.getStrProperty(id, 0, propName);
    }

    public StoryInstance getRunningStory(long actionId) {
        return covenDao.findRunningStory(covenId, id, actionId);
    }

    public void saveStory(StoryInstance storyInstance) {
        covenDao.saveRunningStory(storyInstance);
    }

}
