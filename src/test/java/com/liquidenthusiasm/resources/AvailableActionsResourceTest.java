package com.liquidenthusiasm.resources;

import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AvailableActionsResourceTest {

    AvailableActionsResource rsrc;

    ActionRepo actionRepo;

    AbstractAction doableAction;

    AbstractAction undoableAction;

    StoryFunctionRepo functionRepo;

    private Coven coven;

    @Before
    public void setUp() throws Exception {
        coven = mock(Coven.class);
        actionRepo = mock(ActionRepo.class);
        doableAction = getCovenAction(true);
        when(doableAction.getActionId()).thenReturn(1l);
        undoableAction = getCovenAction(false);
        when(undoableAction.getActionId()).thenReturn(2l);
        when(actionRepo.getCovenActions()).thenReturn(Lists.newArrayList(doableAction, undoableAction));
        when(actionRepo.getCovenActions(any(Coven.class), any(ActionCategory.class))).thenReturn(Lists.newArrayList(doableAction));
        when(actionRepo.getCovenAction(1)).thenReturn(doableAction);
        when(actionRepo.getCovenAction(2)).thenReturn(undoableAction);
        functionRepo = mock(StoryFunctionRepo.class);
        rsrc = new AvailableActionsResource(actionRepo, functionRepo);
    }

    private AbstractAction getCovenAction(boolean canTakeAction) {
        AbstractAction action = mock(AbstractAction.class, RETURNS_DEEP_STUBS);
        when(action.canStartStory(any(Coven.class), any(Person.class))).thenReturn(canTakeAction);
        return action;
    }

    @Test
    public void canRetrieveActions() {
        List<AbstractAction> actions = rsrc.getCovenActions(coven, ActionCategory.CovenAdministration);
        assertEquals(1, actions.size());
        assertEquals(doableAction, actions.get(0));
    }

    @Test
    public void canStartStory() {
        Response response = rsrc.performAction(coven, doableAction.getActionId());
        assertEquals(200, response.getStatus());
        StoryView view = (StoryView) response.getEntity();
        assertNotNull(view);
    }

    @Test
    public void startingStoryWithInvalidActionFails() {
        Response response = rsrc.performAction(coven, undoableAction.getActionId());
        assertEquals(400, response.getStatus());
    }

    @Test
    public void startingStoryWithMissingActionFails() {
        Response response = rsrc.performAction(coven, 999);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void makingChoiceWithNoStartedStoryFails() {
        StoryChoice choice = mock(StoryChoice.class);
        Response response = rsrc.chooseOption(coven, doableAction.getActionId(), choice);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void makingInvalidChoiceForStartedStoryFails() {
        StoryChoice choice = mock(StoryChoice.class, RETURNS_DEEP_STUBS);
        StoryInstance storyInstance = mock(StoryInstance.class, RETURNS_DEEP_STUBS);
        when(doableAction.isValidChoice(storyInstance, choice)).thenReturn(false);
        when(coven.getRunningStory(doableAction.getActionId())).thenReturn(storyInstance);
        Response response = rsrc.chooseOption(coven, doableAction.getActionId(), choice);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void canMakeValidChoiceWithStartedStory() {
        StoryChoice choice = mock(StoryChoice.class, RETURNS_DEEP_STUBS);
        StoryInstance storyInstance = mock(StoryInstance.class, RETURNS_DEEP_STUBS);
        when(doableAction.isValidChoice(storyInstance, choice)).thenReturn(true);
        when(coven.getRunningStory(doableAction.getActionId())).thenReturn(storyInstance);
        Response response = rsrc.chooseOption(coven, doableAction.getActionId(), choice);
        assertEquals(200, response.getStatus());
        verify(doableAction).advanceStory(functionRepo, coven, null, storyInstance, choice);
    }
}