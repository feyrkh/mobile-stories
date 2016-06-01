package com.liquidenthusiasm.domain;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.util.DaosTestUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class CovenTest {

    @Before
    public void setup() {
        DaosTestUtil.setupMockDaos();
    }

    @Test
    public void serializeToJSON() throws Exception {
        final Coven coven = new Coven();
        coven.setDisplayName("Kevin's Kool Koven");
        coven.setPassword("unknown");
        coven.setName("kevinhobbs@gmail.com");
        coven.setAdmin(true);
        coven.setActiveStoryId(32);
        //        coven.setActiveActionCategoryId(1);
        coven.setFocusedPersonId(333l);
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
    public void missingIntPropertyIsNull() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        assertEquals("Missing prop", null, coven.getIntProperty("missing"));
    }

    @Test
    public void canSetIntProperty() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        coven.setIntProperty("prop", 42);
        verify(Daos.propertyDao).updateIntProperty(coven.getId(), 0, "prop", 42);
    }

    @Test
    public void clearIntPropertyOnZeroVal() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        coven.setIntProperty("prop", 0);
        verify(Daos.propertyDao).deleteIntProperty(coven.getId(), 0, "prop");
    }

    @Test
    public void canGetRunningStory() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.setId(3);
        coven.getRunningStory(34);
        verify(Daos.storyDao).findRunningStory(3, 333, 34);
    }

    @Test
    public void saveCallsDao() {
        final Coven coven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        coven.save();
        verify(Daos.covenDao).update(coven);
    }
}