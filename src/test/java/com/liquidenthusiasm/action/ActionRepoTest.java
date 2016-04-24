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
        doableCovenAction = covenAction(true);
        differentCategoryCovenAction = covenAction(true);
        when(differentCategoryCovenAction.getActionCategory()).thenReturn(ActionCategory.InCovenLibrary);
        undoableCovenAction = covenAction(false);
        repo.addCovenAction(undoableCovenAction);
        repo.addCovenAction(doableCovenAction);
        repo.addCovenAction(differentCategoryCovenAction);

    }

    private long id = 1;

    private AbstractAction covenAction(boolean doable) {
        AbstractAction action = mock(AbstractAction.class);
        when(action.canStartStory(any(Coven.class), any(Person.class))).thenReturn(doable);
        when(action.getActionCategory()).thenReturn(ActionCategory.CovenAdministration);
        when(action.getActionId()).thenReturn(id);
        id++;
        return action;
    }

    @Test
    public void canRetrieveDoableActions() {
        List<AbstractAction> covenActions = repo.getCovenActions(coven, ActionCategory.CovenAdministration);
        assertEquals(1, covenActions.size());
        assertEquals(doableCovenAction, covenActions.get(0));
    }

    @Test
    public void canRetrieveDifferentActionCategory() {
        List<AbstractAction> covenActions = repo.getCovenActions(coven, ActionCategory.InCovenLibrary);
        assertEquals(1, covenActions.size());
        assertEquals(differentCategoryCovenAction, covenActions.get(0));

    }

    @Test
    public void canRetrieveAllActions() {
        List<AbstractAction> covenActions = repo.getCovenActions();
        assertEquals(3, covenActions.size());
    }

    @Test(expected = RuntimeException.class)
    public void canNotAddMultipleActionsWithSameId() {
        repo.addCovenAction(doableCovenAction);
        repo.addCovenAction(doableCovenAction);
    }
}