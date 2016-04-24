package com.liquidenthusiasm.story;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractStoryGeneratorTest {

    private static long actionId = new Random().nextInt();

    private static class GenericStoryGenerator extends AbstractAction {

        public GenericStoryGenerator() {
            super(actionId, ActionCategory.CovenAdministration, "Generic", "GenericDesc");
        }

        @Override public void initializeStory(StoryInstance story) {
            story.getState().put("created", true);
        }

        @Override public void advanceStory(Coven coven, StoryInstance story, StoryChoice choice) {
            story.setStoryPosition(choice.getChoiceId());
        }

        @Override public void generateStoryTextAndOptions(StoryView view, StoryInstance instance) {
            view.setOptions(new ArrayList<>());
            view.setStoryText("Neat!");
        }

        @Override public boolean canStartStory(Coven actor, Person person) {
            return true;
        }
    }

    AbstractAction gen;

    Coven coven;

    @Before
    public void setup() {
        gen = new GenericStoryGenerator();
        coven = mock(Coven.class);
    }

    @Test
    public void existingStoryDoesNotGenerateNewInstance() {
        StoryInstance existingStory = mock(StoryInstance.class);
        when(coven.getRunningStory(actionId)).thenReturn(existingStory);
        StoryInstance retrieved = gen.getOrGenerateStoryInstance(coven);
        assertSame(existingStory, retrieved);
        verify(coven, never()).saveStory(any(StoryInstance.class));
    }

    @Test
    public void nonexistingStoryGeneratesNewInstance() {
        StoryInstance retrieved = gen.getOrGenerateStoryInstance(coven);
        assertNotNull(retrieved);
        assertEquals(true, retrieved.getState().get("created"));
        assertEquals(actionId, retrieved.getActionId());
        verify(coven).saveStory(any(StoryInstance.class));
    }

    @Test
    public void canGenerateViewFromInstance() {
        StoryInstance instance = gen.getOrGenerateStoryInstance(coven);
        assertNotNull(instance);
        StoryView view = gen.getStoryView(instance);
        assertEquals("Neat!", view.getStoryText());
        assertEquals(instance.getCovenId(), view.getCovenId());
        assertEquals(instance.getPersonId(), view.getPersonId());
        assertEquals(instance.getActionId(), view.getActionId());
    }

}