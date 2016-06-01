package com.liquidenthusiasm.action;

import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.action.story.FieldDef;
import com.liquidenthusiasm.action.story.FieldDefSelectOption;
import com.liquidenthusiasm.action.story.JsonStory;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.*;
import com.liquidenthusiasm.util.DaosTestUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JsonStoryTest {

    static JsonStory story;

    Coven coven;

    Person person;

    private StoryInstance instance;

    private StoryView view;

    @BeforeClass
    public static void setupStory() {
        story = FixtureTestUtil.loadFixture("fixtures/jsonStory.json", JsonStory.class);
    }

    @Before
    public void setup() {
        DaosTestUtil.setupMockDaos();
        coven = mock(Coven.class);
        person = mock(Person.class);
        when(person.getTimeRemaining()).thenReturn(24);
        StoryFunctionRepo storyFunctionRepo = new StoryFunctionRepo();
        Daos.functionRepo = storyFunctionRepo;
        storyFunctionRepo.put("randomPersonName", (args, c, p, s) -> {
            s.state("ss_randomPersonName", "Rando Cardrissian");

        });
        storyFunctionRepo.put("registerStudent", (args, c, p, s) -> {
            String name = (String) args.get("studentName");
            String focus = (String) args.get("studentFocus");
            s.state("registeredName", name);
            s.state("registeredFocus", focus);
            s.state("si_registerStudent", 1);
        });
        when(coven.getId()).thenReturn(1l);
        instance = story.getOrGenerateStoryInstance(coven, person);
        view = story.getStoryView(instance, coven, person);
    }

    @Test
    public void canDeserialize() {
        assertEquals(1, story.getActionId());
        assertEquals(ActionCategory.Ledgers, story.getActionCategory());
        assertEquals("Accept Students", story.getActionName());
        assertEquals("There's room for more students...perhaps we should recruit some?", story.getActionDescription());
        assertEquals(2, story.getEnableTriggers().length);
        assertEquals(3, story.getStoryStates().length);
        FieldDef selectOption = story.getStoryStates()[0].getOptions()[0].getFields()[1];
        assertNotNull("selectOption", selectOption);
        FieldDefSelectOption[] values = selectOption.getOptions();
        assertNotNull("selectOption.values", values);
        assertEquals("selectOption.values.length", 2, values.length);
    }

    @Test
    public void hasSanitizedView() {
        Map<String, Object> sanitized = story.getCleanView();
        assertNotNull(sanitized);
        assertNull(sanitized.get("states"));
    }

    @Test
    public void canStartStoryBecauseLivingSpace() {
        when(coven.getIntProperty("ci_living_space")).thenReturn(3);
        when(coven.getIntProperty("ci_members")).thenReturn(3);
        assertTrue(story.canStartStory(coven, null));
    }

    @Test
    public void canStartStoryBecauseNoMembers() {
        when(coven.getIntProperty("ci_living_space")).thenReturn(0);
        when(coven.getIntProperty("ci_members")).thenReturn(0);
        assertTrue(story.canStartStory(coven, null));
    }

    @Test
    public void canNotStartStory() {
        when(coven.getIntProperty("ci_living_space")).thenReturn(0);
        when(coven.getIntProperty("ci_members")).thenReturn(3);
        assertFalse(story.canStartStory(coven, null));
    }

    @Test
    public void actuallyStartedStory() {
        assertEquals(1, view.getActionId());
        assertEquals(1, view.getCovenId());
        assertEquals(2, view.getOptions().length);
        assertEquals(0, view.getPersonId());
        assertEquals("Accept new students", view.getHeading());
        assertEquals("Your office is flooded with letters from prospective students. All you need do is send acceptance letters.",
            view.getStoryText());
        verify(Daos.storyDao).saveRunningStory(instance);
    }

    @Test
    public void cancelImmediately() {
        assertEquals("Starting at state 0", 0, instance.getStoryPosition());
        story.advanceStory(Daos.functionRepo, coven, null, instance, new StoryChoice(instance.getActionId(), 1));
        view = story.getStoryView(instance, coven, person);
        assertEquals("Canceled, should be at state 2", 2, instance.getStoryPosition());
        assertEquals("None of them are suitable!", view.getHeading());
        assertEquals("Perhaps this can wait.", view.getStoryText());
        assertEquals(null, view.getOptions());
    }

    @Test
    public void preSubmitCallsAreExecuted() {
        assertEquals("Rando Cardrissian", instance.state("ss_randomPersonName"));
    }

    @Test
    public void optionsGetDefaultValueSubstitution() {
        assertEquals("Rando Cardrissian", view.getOptions()[0].getFields()[0].getDefaultValue());
    }

    @Test
    public void acceptStudent() {
        verify(Daos.storyDao).saveRunningStory(instance);
        story.advanceStory(Daos.functionRepo, coven, person, instance,
            new StoryChoice(instance.getActionId(), 0).formValue("ss_name", "Kevin").formValue("ss_focus", "experimentation"));
        view = story.getStoryView(instance, coven, person);
        assertEquals("Kevin", instance.state("registeredName"));
        assertEquals("experimentation", instance.state("registeredFocus"));
        assertEquals(1, instance.getStoryPosition());
        assertNull(instance.getFlash());
        assertNull(view.getFlash());

        view = story.getStoryView(instance, coven, person);
        assertEquals("The letter is posted!", view.getHeading());
    }

    @Test
    public void failToAcceptStudentWithShortName() {
        story.advanceStory(Daos.functionRepo, coven, person, instance,
            new StoryChoice(instance.getActionId(), 0).formValue("ss_name", "A").formValue("ss_focus", "experimentation"));
        assertNotNull("Instance flash should be non-null before getting view", instance.getFlash());
        assertNull("View flash should be null before getting view", view.getFlash());
        view = story.getStoryView(instance, coven, person);
        assertEquals(0, instance.getStoryPosition());
        assertEquals(null, instance.state("registeredName"));
        assertEquals(null, instance.state("registeredFocus"));
        assertNotNull("Instance flash should be non-null after getting view", instance.getFlash());
        assertNotNull("View flash should be non-null after getting view", view.getFlash());
        assertFalse("contains ss_name: " + view.getFlash(), view.getFlash().contains("ss_name"));
        assertTrue("contains Name: " + view.getFlash(), view.getFlash().contains("Name"));
    }

    @Test
    public void failToAcceptStudentWithLongName() {
        story.advanceStory(Daos.functionRepo, coven, person, instance,
            new StoryChoice(instance.getActionId(), 0).formValue("ss_name", "TheVeryBadNoGoodExtraLongNameWithTooManyCharacters")
                .formValue("ss_focus", "experimentation"));
        assertNotNull("Instance flash should be non-null before getting view", instance.getFlash());
        assertNull("View flash should be null before getting view", view.getFlash());
        view = story.getStoryView(instance, coven, person);
        assertEquals(0, instance.getStoryPosition());
        assertEquals(null, instance.state("registeredName"));
        assertEquals(null, instance.state("registeredFocus"));
        assertNotNull("Instance flash should be non-null after getting view", instance.getFlash());
        assertNotNull("View flash should be non-null after getting view", view.getFlash());
        assertFalse("contains ss_name: " + view.getFlash(), view.getFlash().contains("ss_name"));
        assertTrue("contains Name: " + view.getFlash(), view.getFlash().contains("Name"));
    }

    @Test
    public void failToAcceptStudentWithInvalidFocus() {
        story.advanceStory(Daos.functionRepo, coven, person, instance,
            new StoryChoice(instance.getActionId(), 0).formValue("ss_name", "Kevin").formValue("ss_focus", "eXpErImEnTaTiOn"));
        assertNotNull("Instance flash should be non-null before getting view", instance.getFlash());
        assertNull("View flash should be null before getting view", view.getFlash());
        view = story.getStoryView(instance, coven, person);
        assertEquals(0, instance.getStoryPosition());
        assertEquals(null, instance.state("registeredName"));
        assertEquals(null, instance.state("registeredFocus"));
        assertNotNull("Instance flash should be non-null after getting view", instance.getFlash());
        assertNotNull("View flash should be non-null after getting view", view.getFlash());
        assertFalse("contains ss_focus: " + view.getFlash(), view.getFlash().contains("ss_focus"));
        assertTrue("contains Focus: " + view.getFlash(), view.getFlash().contains("Focus"));
    }

    @Test
    public void invalidChoiceDoesNotProgress() {
        story.advanceStory(Daos.functionRepo, coven, person, instance,
            new StoryChoice(instance.getActionId(), 33));
        assertNotNull("Instance flash should be non-null before getting view", instance.getFlash());
        assertEquals(0, instance.getStoryPosition());

    }

    @Test
    public void invalidActionIdDoesNotProgress() {
        story.advanceStory(Daos.functionRepo, coven, person, instance,
            new StoryChoice(instance.getActionId() + 1, 1));
        assertNotNull("Instance flash should be non-null before getting view", instance.getFlash());
        assertEquals(0, instance.getStoryPosition());

    }
}