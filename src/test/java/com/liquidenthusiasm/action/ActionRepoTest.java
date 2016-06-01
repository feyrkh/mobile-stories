package com.liquidenthusiasm.action;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActionRepoTest {

    ActionRepo repo;

    AbstractAction undoableCovenAction;

    AbstractAction doableCovenAction;

    AbstractAction differentCategoryCovenAction;

    private Coven coven;

    @Before
    public void setup() {
        coven = mock(Coven.class);
        repo = new ActionRepo();
        doableCovenAction = covenAction(true, "doable");
        differentCategoryCovenAction = covenAction(true, "different");
        when(differentCategoryCovenAction.getActionCategory()).thenReturn(ActionCategory.Library);
        undoableCovenAction = covenAction(false, "undoable");
        repo.addAction(undoableCovenAction);
        repo.addAction(doableCovenAction);
        repo.addAction(differentCategoryCovenAction);

    }

    private long id = 1;

    private AbstractAction covenAction(boolean doable, String name) {
        AbstractAction action = mock(AbstractAction.class);
        when(action.getActionName()).thenReturn(name);
        when(action.canStartStory(any(Coven.class), any(Person.class))).thenReturn(doable);
        when(action.getActionCategory()).thenReturn(ActionCategory.Ledgers);
        when(action.getActionId()).thenReturn(id);
        id++;
        return action;
    }

    @Test
    public void canRetrieveDoableActions() {
        List<AbstractAction> covenActions = repo.getActions(coven, null, ActionCategory.Ledgers);
        assertEquals(1, covenActions.size());
        assertEquals(doableCovenAction, covenActions.get(0));
    }

    @Test
    public void canRetrieveDifferentActionCategory() {
        List<AbstractAction> covenActions = repo.getActions(coven, null, ActionCategory.Library);
        assertEquals(1, covenActions.size());
        assertEquals(differentCategoryCovenAction, covenActions.get(0));

    }

    @Test
    public void canRetrieveAllActions() {
        List<AbstractAction> covenActions = repo.getActions();
        assertEquals(3, covenActions.size());
    }

    @Test(expected = RuntimeException.class)
    public void canNotAddMultipleActionsWithSameId() {
        repo.addAction(doableCovenAction);
        repo.addAction(doableCovenAction);
    }
}