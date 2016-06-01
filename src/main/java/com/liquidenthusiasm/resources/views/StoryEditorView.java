package com.liquidenthusiasm.resources.views;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.dao.Daos;
import io.dropwizard.views.View;

public class StoryEditorView extends View {

    private static final Logger log = LoggerFactory.getLogger(StoryEditorView.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    public StoryEditorView() {
        super("storyEditor.ftl");
    }

    public String getVarRepo() throws JsonProcessingException {
        return mapper.writeValueAsString(Daos.varRepo.getVars());
    }

    public String getActionRepo() throws JsonProcessingException {
        return mapper.writeValueAsString(Daos.actionRepo.getActions());
    }

    public String getFunctionRepo() throws JsonProcessingException {
        return mapper.writeValueAsString(Daos.functionRepo.getFunctions());
    }

    public long getNextStoryId() {
        long retval = 0;
        for (AbstractAction abstractAction : Daos.actionRepo.getActions()) {
            if (abstractAction.getActionId() >= retval) {
                retval = abstractAction.getActionId() + 1;
            }
        }
        return retval;
    }

    public String getActionCategorySelectOptions() {
        return Stream.of(ActionCategory.values())
            .map((ac) -> String.format("<option value='%s'>%s</option>", ac.name(), ac.name()))
            .collect(Collectors.joining());
    }
}
