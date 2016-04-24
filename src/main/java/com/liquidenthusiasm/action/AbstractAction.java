package com.liquidenthusiasm.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.domain.*;
import com.liquidenthusiasm.story.StoryGenerator;

public abstract class AbstractAction implements StoryGenerator {

    Logger log = LoggerFactory.getLogger(AbstractAction.class);

    private ActionCategory actionCategory;

    private long actionId;

    private String actionName;

    private String actionDescription;

    protected AbstractAction(long actionId, ActionCategory covenAdministration, String actionName, String actionDescription) {
        this.actionId = actionId;
        this.actionName = actionName;
        this.actionDescription = actionDescription;
        this.actionCategory = covenAdministration;
    }

    public AbstractAction() {

    }

    public abstract boolean canStartStory(Coven coven, Person person);

    public ActionCategory getActionCategory() {
        return actionCategory;
    }

    public long getActionId() {
        return actionId;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    @Override public StoryInstance getOrGenerateStoryInstance(Coven coven) {
        StoryInstance running = coven.getRunningStory(getActionId());
        if (running == null) {
            log.info("Creating new story instance for covenId={}, actionId={}", coven.getId(), getActionId());
            running = new StoryInstance();
            running.setState(new HashMap<>());
            running.setPersonId(0);
            running.setCovenId(coven.getId());
            running.setActionId(this.getActionId());
            running.setStoryPosition(0);
            initializeStory(running);
            coven.saveStory(running);
        }
        return running;
    }

    @Override public StoryInstance getOrGenerateStoryInstance(Coven coven, Person person) {
        //        StoryInstance running = Person.getRunningStory(getActionId());
        //        return running;
        return null;
    }

    @Override public StoryView getStoryView(StoryInstance instance) {
        StoryView view = new StoryView();
        view.setActionId(instance.getActionId());
        view.setCovenId(instance.getCovenId());
        view.setPersonId(instance.getPersonId());
        generateStoryTextAndOptions(view, instance);
        return view;
    }

    @Override public boolean isValidChoice(StoryInstance instance, StoryChoice choice) {
        return choice != null;
    }

    public StoryInstance startStory(Coven coven, Person person) {
        return getOrGenerateStoryInstance(coven, person);
    }

    public static String story(String filename, Map<String, Object> state) {
        try {
            String result = Resources.toString(Resources.getResource(filename), Charsets.UTF_8).trim();
            for (Map.Entry<String, Object> entry : state.entrySet()) {
                String token = String.format("\\{\\{%s\\}\\}", entry.getKey());
                result = result.replaceAll(token, String.valueOf(entry.getValue()));
            }
            return result;
        } catch (IOException e) {
            //            log.error("Missing story filename: " + filename, e);
            return "Missing story filename: " + filename;
        }
    }

}
