package com.liquidenthusiasm.domain;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.dao.CovenDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CovenTest {

    private CovenDao covenDao;

    @Before
    public void setup() {
        covenDao = mock(CovenDao.class);
        Coven.covenDao = covenDao;
    }

    @Test
    public void serializeToJSON() throws Exception {
        final Coven coven = new Coven();
        coven.setDisplayName("Kevin's Kool Koven");
        coven.setPassword("unknown");
        coven.setName("kevinhobbs@gmail.com");
        coven.setAdmin(true);
        FixtureTestUtil.verifyAgainstFixture("fixtures/coven.json", Coven.class, coven);
        System.out.println(coven);
    }

    @Test
    public void sanitizeRemovesPassword() throws IOException {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        assertNotNull("before sanitize", coven.getPassword());
        coven.sanitize();
        assertNull("after sanitize", coven.getPassword());
    }

    @Test
    public void missingIntPropertyIsZero() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        assertEquals("Missing prop", 0, coven.getIntProperty("missing"));
    }

    @Test
    public void canSetIntProperty() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        coven.setIntProperty("prop", 42);
        verify(covenDao).updateIntProperty(coven.getId(), "prop", 42);
    }

    @Test
    public void clearIntPropertyOnZeroVal() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        coven.setIntProperty("prop", 0);
        verify(covenDao).deleteIntProperty(coven.getId(), "prop");
    }

    @Test
    public void canGetRunningStory() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        coven.getRunningStory(34);
        verify(covenDao).findRunningStory(3, 34);
    }

    @Test
    public void canSaveStory() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        StoryInstance story = mock(StoryInstance.class);
        coven.saveStory(story);
        verify(covenDao).saveRunningStory(story);
    }
}