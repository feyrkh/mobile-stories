package com.liquidenthusiasm.resources;

import java.util.HashMap;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.resources.views.RenderedStoryViewContext;
import com.liquidenthusiasm.util.DaosTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AvailableActionsResourceTest {

    AvailableActionsResource rsrc;

    AbstractAction doableAction;

    AbstractAction undoableAction;

    private Coven coven;

    @Before
    public void setUp() throws Exception {
        DaosTestUtil.setupMockDaos();
        coven = mock(Coven.class);
        doableAction = getCovenAction(true);
        when(doableAction.getActionId()).thenReturn(1l);
        undoableAction = getCovenAction(false);
        when(undoableAction.getActionId()).thenReturn(2l);
        when(Daos.actionRepo.getActions()).thenReturn(Lists.newArrayList(doableAction, undoableAction));
        when(Daos.actionRepo.getActions(any(Coven.class), any(Person.class), any(ActionCategory.class))).thenReturn(
            Lists.newArrayList(doableAction));
        when(Daos.actionRepo.getCleanActions(any(Coven.class), any(Person.class), any(ActionCategory.class)))
            .thenReturn(Lists.newArrayList(new HashMap<String, Object>() {{
                put("actionId", 1l);
            }}));
        when(Daos.actionRepo.getAction(1)).thenReturn(doableAction);
        when(Daos.actionRepo.getAction(2)).thenReturn(undoableAction);
        rsrc = new AvailableActionsResource();
    }

    private AbstractAction getCovenAction(boolean canTakeAction) {
        AbstractAction action = mock(AbstractAction.class, RETURNS_DEEP_STUBS);
        when(action.canStartStory(any(Coven.class), any(Person.class))).thenReturn(canTakeAction);
        return action;
    }
    //
    //    @Test
    //    public void canRetrieveActions() {
    //        List<Map<String, Object>> actions = rsrc.getCovenActions(coven, null, ActionCategory.Ledgers);
    //        assertEquals(1, actions.size());
    //        assertEquals(doableAction.getActionId(), actions.get(0).get("actionId"));
    //    }

    @Test
    public void canStartStory() {
        Response response = rsrc.performAction(coven, doableAction.getActionId());
        assertEquals(200, response.getStatus());
        RenderedStoryViewContext view = (RenderedStoryViewContext) response.getEntity();
        assertNotNull(view);
    }

    @Test
    public void startingStorySetsCovensActiveStoryId() {
        Response response = rsrc.performAction(coven, doableAction.getActionId());
        assertEquals(200, response.getStatus());
        verify(coven).setActiveStoryId(doableAction.getActionId());
        verify(coven).save();
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
        verify(doableAction).advanceStory(Daos.functionRepo, coven, null, storyInstance, choice);
    }
}