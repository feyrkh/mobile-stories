package com.liquidenthusiasm.action.story;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;
import com.liquidenthusiasm.util.DaosTestUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractStoryGeneratorTest {

    private static long actionId = new Random().nextInt();

    private static class GenericStoryGenerator extends AbstractAction {

        public GenericStoryGenerator() {
            super(actionId, ActionCategory.Ledgers, "Generic", "GenericDesc");
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
        DaosTestUtil.setupMockDaos();
        gen = new GenericStoryGenerator();
        coven = mock(Coven.class);
    }

    @Test
    public void existingStoryDoesNotGenerateNewInstance() {
        StoryInstance existingStory = mock(StoryInstance.class);
        when(coven.getRunningStory(actionId)).thenReturn(existingStory);
        StoryInstance retrieved = gen.getOrGenerateStoryInstance(coven, null);
        assertSame(existingStory, retrieved);
        verify(Daos.storyDao, never()).saveRunningStory(any(StoryInstance.class));
    }

    @Test
    public void nonexistingStoryGeneratesNewInstance() {
        StoryInstance retrieved = gen.getOrGenerateStoryInstance(coven, null);
        assertNotNull(retrieved);
        assertEquals(true, retrieved.getState().get("created"));
        assertEquals(actionId, retrieved.getActionId());
        verify(Daos.storyDao).saveRunningStory(any(StoryInstance.class));
    }

    @Test
    public void canGenerateViewFromInstance() {
        StoryInstance instance = gen.getOrGenerateStoryInstance(coven, null);
        assertNotNull(instance);
        StoryView view = gen.getStoryView(instance, coven, null);
        assertEquals("Neat!", view.getStoryText());
        assertEquals(instance.getCovenId(), view.getCovenId());
        assertEquals(instance.getPersonId(), view.getPersonId());
        assertEquals(instance.getActionId(), view.getActionId());
    }

}