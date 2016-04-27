package com.liquidenthusiasm.story;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractStoryGeneratorTest {

    private static long actionId = new Random().nextInt();

    private StoryFunctionRepo functionRepo;

    private static class GenericStoryGenerator extends AbstractAction {

        public GenericStoryGenerator() {
            super(actionId, ActionCategory.CovenAdministration, "Generic", "GenericDesc");
        }

        @Override public void initializeStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story) {
            story.getState().put("created", true);

        }

        @Override
        public void advanceStory(StoryFunctionRepo storyFunctionRepo, Coven coven, Person person, StoryInstance story, StoryChoice choice) {
            story.setStoryPosition(choice.getChoiceId());
        }

        @Override public void generateStoryTextAndOptions(StoryView view, StoryInstance instance, Coven coven, Person person) {
            view.setOptions(null);
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
        functionRepo = mock(StoryFunctionRepo.class);
    }

    @Test
    public void existingStoryDoesNotGenerateNewInstance() {
        StoryInstance existingStory = mock(StoryInstance.class);
        when(coven.getRunningStory(actionId)).thenReturn(existingStory);
        StoryInstance retrieved = gen.getOrGenerateStoryInstance(functionRepo, coven, null);
        assertSame(existingStory, retrieved);
        verify(coven, never()).saveStory(any(StoryInstance.class));
    }

    @Test
    public void nonexistingStoryGeneratesNewInstance() {
        StoryInstance retrieved = gen.getOrGenerateStoryInstance(functionRepo, coven, null);
        assertNotNull(retrieved);
        assertEquals(true, retrieved.getState().get("created"));
        assertEquals(actionId, retrieved.getActionId());
        verify(coven).saveStory(any(StoryInstance.class));
    }

    @Test
    public void canGenerateViewFromInstance() {
        StoryInstance instance = gen.getOrGenerateStoryInstance(functionRepo, coven, null);
        assertNotNull(instance);
        StoryView view = gen.getStoryView(instance, coven, null);
        assertEquals("Neat!", view.getStoryText());
        assertEquals(instance.getCovenId(), view.getCovenId());
        assertEquals(instance.getPersonId(), view.getPersonId());
        assertEquals(instance.getActionId(), view.getActionId());
    }

}