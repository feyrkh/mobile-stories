package com.liquidenthusiasm.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.FixtureTestUtil;
import com.liquidenthusiasm.domain.StoryInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class CovenDaoIT extends AbstractDaoTest {

    CovenDao dao;

    Coven c;

    long covenId, personId, actionId;

    @Before
    public void setup() {
        covenId = 5;
        personId = 1;
        actionId = 42;
        dao = dbi.onDemand(CovenDao.class);
        c = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
    }

    @After
    public void clearTables() {
        resetDb();
    }

    @Test
    public void canSave() {
        long id = dao.insert(c);
        assertEquals(1, id);
    }

    @Test(expected = UnableToExecuteStatementException.class)
    public void canNotHaveDuplicateNames() {
        dao.insert(c);
        dao.insert(c);
    }

    @Test
    public void saveLoadSerializesCorrectly() {
        long id = dao.insert(c);
        c.setId(id);
        Coven retrieved = dao.findById(id);
        assertEqualCovens(retrieved, c);
    }

    private void assertEqualCovens(Coven actual, Coven expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected);
    }

    @Test
    public void canFindById() {
        long id = dao.insert(c);
        c.setId(id);
        Coven retrieved = dao.findById(id);
        assertEqualCovens(retrieved, c);
        retrieved = dao.findById(id + 1);
        assertThat(retrieved).isNull();
    }

    @Test
    public void canFindByName() {
        long id = dao.insert(c);
        c.setId(id);
        Coven retrieved = dao.findByName(c.getName());
        assertEqualCovens(retrieved, c);
        retrieved = dao.findByName("no name mcgee");
        assertThat(retrieved).isNull();
    }

    @Test
    public void canSetIntProperty() {
        dao.updateIntProperty(covenId, "myProp", 30);
        int val = dao.getIntProperty(covenId, "myProp");
        assertThat(val).isEqualTo(30);
    }

    @Test
    public void canUpdateIntProperty() {
        dao.updateIntProperty(covenId, "myProp", 30);
        int val = dao.getIntProperty(covenId, "myProp");
        assertThat(val).isEqualTo(30);

        dao.updateIntProperty(covenId, "myProp", 42);
        val = dao.getIntProperty(covenId, "myProp");
        assertThat(val).isEqualTo(42);
    }

    @Test
    public void canDeleteIntProperty() {
        dao.updateIntProperty(covenId, "myProp", 30);
        int val = dao.getIntProperty(covenId, "myProp");
        assertThat(val).isEqualTo(30);

        dao.deleteIntProperty(covenId, "myProp");
        val = dao.getIntProperty(covenId, "myProp");
        assertThat(val).isEqualTo(0);
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
        StoryInstance retrieved = dao.findRunningStory(covenId, actionId);
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
