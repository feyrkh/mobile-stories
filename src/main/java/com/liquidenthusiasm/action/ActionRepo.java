package com.liquidenthusiasm.action;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.liquidenthusiasm.domain.Coven;

public class ActionRepo {

    private List<AbstractAction> covenActions = new ArrayList<>();

    private Map<Long, AbstractAction> covenActionsById = new HashMap<>();

    public List<AbstractAction> getCovenActions() {
        return ImmutableList.copyOf(covenActions);
    }

    public List<AbstractAction> getCovenActions(Coven coven, ActionCategory category) {
        return covenActions.parallelStream()
            .filter((ca) -> ca.getActionCategory().equals(category))
            .filter((ca) -> ca.canStartStory(coven, null))
            .collect(Collectors.toList());
    }

    public void addCovenAction(AbstractAction action) {
        if (covenActionsById.containsKey(action.getActionId())) {
            throw new RuntimeException(
                "Can't add action " + action.getClass().getSimpleName() + " to actions list, already have one with the same ID: "
                    + covenActionsById.get(action.getActionId()).getClass().getSimpleName());
        }
        covenActionsById.put(action.getActionId(), action);
        covenActions.add(action);
    }

    public AbstractAction getCovenAction(long actionId) {
        Optional<AbstractAction> found =
            covenActions.parallelStream().filter((ca) -> ca.getActionId() == actionId).findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        return null;
    }
}
