package com.liquidenthusiasm.action;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.liquidenthusiasm.action.story.JsonStory;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

public class ActionRepo {

    private static final Logger log = LoggerFactory.getLogger(ActionRepo.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private Set<AbstractAction> actions = new TreeSet<>(
        new Comparator<AbstractAction>() {

            @Override public int compare(AbstractAction o1, AbstractAction o2) {
                return o1.getActionName().compareTo(o2.getActionName());
            }
        });

    private Map<Long, AbstractAction> actionsById = new HashMap<>();

    public List<AbstractAction> getActions() {
        return ImmutableList.copyOf(actions);
    }

    public List<AbstractAction> getActions(Coven coven, Person person, ActionCategory category) {
        return actions.parallelStream()
            .filter((ca) -> ca.getActionCategory().equals(category))
            .filter((ca) -> ca.canStartStory(coven, person))
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getCleanActions(Coven coven, Person person, ActionCategory category) {
        return getActions(coven, person, category)
            .parallelStream()
            .map(AbstractAction::getCleanView)
            .collect(Collectors.toList());
    }

    public void addAction(AbstractAction action) {
        if (actionsById.containsKey(action.getActionId())) {
            throw new RuntimeException(
                "Can't add action with actionId=" + action.getActionId() + " actionName='" + action.getActionName()
                    + "' to actions list, already have one with the same ID, existingActionName='"
                    + actionsById.get(action.getActionId()).getActionName() + "'");
        }
        actionsById.put(action.getActionId(), action);
        actions.add(action);
    }

    public void addAction(String filename) throws IOException {
        log.info("Loading JSON story {}", filename);
        String json = Resources.toString(Resources.getResource(filename), Charsets.UTF_8);
        JsonStory story = mapper.readValue(json, JsonStory.class);
        story.setFilename(filename);
        story.validate();
        addAction(story);
    }

    public AbstractAction getAction(long actionId) {
        Optional<AbstractAction> found =
            actions.parallelStream().filter((ca) -> ca.getActionId() == actionId).findFirst();
        if (found.isPresent()) {
            return found.get();
        }
        return null;
    }
}
