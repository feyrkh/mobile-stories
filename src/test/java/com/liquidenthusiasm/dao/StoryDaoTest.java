package com.liquidenthusiasm.dao;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.domain.FixtureTestUtil;
import com.liquidenthusiasm.domain.StoryInstance;

import static org.assertj.core.api.Assertions.assertThat;

public class StoryDaoTest extends AbstractDaoTest {

    StoryDao dao;

    long covenId, personId, actionId;

    @Before
    public void setup() {
        covenId = 5;
        personId = 1;
        actionId = 42;
        dao = dbi.onDemand(StoryDao.class);
    }

    @Test
    public void canFindStoryByPerson() {
        StoryInstance story = FixtureTestUtil.loadFixture("fixtures/storyInstance.json", StoryInstance.class);
        dao.saveRunningStory(story);
        StoryInstance retrieved = dao.findRunningStory(covenId, personId, actionId);
        assertThat(retrieved).isEqualToComparingFieldByField(story);
    }

    @Test
    public void canFindStoryByCoven() {
        StoryInstance story = FixtureTestUtil.loadFixture("fixtures/storyInstance.json", StoryInstance.class);
        story.setPersonId(0);
        dao.saveRunningStory(story);
        StoryInstance retrieved = dao.findRunningStory(covenId, 0, actionId);
        assertThat(retrieved).isEqualToComparingFieldByField(story);
    }

    @Test
    public void canDeleteStory() {
        StoryInstance story = FixtureTestUtil.loadFixture("fixtures/storyInstance.json", StoryInstance.class);
        dao.saveRunningStory(story);
        StoryInstance retrieved = dao.findRunningStory(covenId, personId, actionId);
        assertThat(retrieved).isNotNull();
        dao.deleteStory(story);
        retrieved = dao.findRunningStory(covenId, personId, actionId);
        assertThat(retrieved).isNull();
    }
}