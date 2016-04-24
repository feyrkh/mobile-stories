package com.liquidenthusiasm.action;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.FixtureTestUtil;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonStoryTest {

    static JsonStory story;

    Coven coven;

    private StoryInstance instance;

    private StoryView view;

    @BeforeClass
    public static void setupStory() {
        story = FixtureTestUtil.loadFixture("fixtures/jsonStory.json", JsonStory.class);
    }

    @Before
    public void setup() {
        coven = mock(Coven.class);
        when(coven.getId()).thenReturn(1l);
        instance = story.getOrGenerateStoryInstance(coven);
        view = story.getStoryView(instance);
    }

    @Test
    public void canDeserialize() {
        assertEquals(1, story.getActionId());
        assertEquals(ActionCategory.CovenAdministration, story.getActionCategory());
        assertEquals("Accept Students", story.getActionName());
        assertEquals("There's room for more mages...perhaps we should recruit some?", story.getActionDescription());
        assertEquals(2, story.getStoryTriggers().length);
        assertEquals(3, story.getStoryStates().length);
    }

    @Test
    public void canStartStoryBecauseLivingSpace() {
        when(coven.getIntProperty("ci_Living Space")).thenReturn(3);
        when(coven.getIntProperty("ci_Members")).thenReturn(3);
        assertTrue(story.canStartStory(coven, null));
    }

    @Test
    public void canStartStoryBecauseNoMembers() {
        when(coven.getIntProperty("ci_Living Space")).thenReturn(0);
        when(coven.getIntProperty("ci_Members")).thenReturn(0);
        assertTrue(story.canStartStory(coven, null));
    }

    @Test
    public void canNotStartStory() {
        when(coven.getIntProperty("ci_Living Space")).thenReturn(0);
        when(coven.getIntProperty("ci_Members")).thenReturn(3);
        assertFalse(story.canStartStory(coven, null));
    }

    @Test
    public void actuallyStartStory() {
        assertEquals(1, view.getActionId());
        assertEquals(1, view.getCovenId());
        assertEquals(2, view.getOptions().size());
        assertEquals(0, view.getPersonId());
        assertEquals("Accept Students", view.getStoryName());
        assertEquals("There's room for more students...perhaps we should recruit some?", view.getStoryText());
    }

    @Test
    public void cancelImmediately() {
        fail();
    }

    @Test
    public void acceptStudent() {
        fail();
    }

    @Test
    public void failToAcceptStudentWithShortName() {
        fail();
    }

    @Test
    public void failToAcceptStudentWithLongName() {
        fail();
    }

    @Test
    public void failToAcceptStudentWithInvalidFocus() {
        fail();
    }

    @Test
    public void invalidChoiceDoesNotProgress() {
        fail();
    }
}