package com.liquidenthusiasm.resources;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.CovenApplication;
import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;
import com.liquidenthusiasm.resources.views.RenderedStoryViewContext;
import io.dropwizard.auth.Auth;

@Path("/actions")
@Produces(MediaType.APPLICATION_JSON)
public class AvailableActionsResource {

    private static final Logger log = LoggerFactory.getLogger(AvailableActionsResource.class);

    public AvailableActionsResource() {
    }

    @GET
    @Path("refresh")
    public String refresh() throws IOException {
        CovenApplication.refreshVariableDefinitions();
        CovenApplication.refreshActions();
        return "Actions reloaded";
    }

    @POST
    @Path("{actionId}/perform")
    public Response performAction(@Auth Coven coven, @PathParam("actionId") long actionId) {
        AbstractAction action = Daos.actionRepo.getAction(actionId);
        Person person = null;
        if (coven.getFocusedPersonId() > 0) {
            person = Daos.personDao.findById(coven.getFocusedPersonId());
        }
        if (action == null) {
            log.error("Nonexistent actionId={} chosen, with covenId={}", actionId, coven.getId());
            return getBadRequestResponse(coven, action, person);
        }
        if (!action.canStartStory(coven, null)) {
            log.error("actionId={} chosen which isn't valid for covenId={}", actionId, coven.getId());
            return getBadRequestResponse(coven, action, person);
        }
        StoryInstance storyInstance = action.getOrGenerateStoryInstance(coven, person);
        StoryView storyView = action.getStoryView(storyInstance, coven, person);

        if (person == null) {
            coven.setActiveStoryId(action.getActionId());
            coven.save();
        } else {
            person.setActiveStoryId(action.getActionId());
            person.save();
            coven.save();
        }

        return Response.ok(new RenderedStoryViewContext(
            action, storyView, coven, person, storyInstance, null
        )).build();
    }

    private Response getBadRequestResponse(Coven coven, AbstractAction action, Person person) {
        return Response.status(Response.Status.BAD_REQUEST).entity(
            new RenderedStoryViewContext(action, null, coven, person, null,
                Daos.actionRepo.getActions(coven, person, person == null ? ActionCategory.Atelier : person.getCurrentCategory()))
        ).build();
    }

    private Response getDeletedStoryResponse(Coven coven, Person person) {
        return Response.ok().entity(
            new RenderedStoryViewContext(null, null, coven, person, null,
                Daos.actionRepo.getActions(coven, person, person.getCurrentCategory()))
        ).build();
    }

    @POST
    @Path("travel/{categoryId}")
    public Response travel(@Auth Coven coven, @PathParam("categoryId") long categoryId) {
        Person person = coven.getFocusedPerson();
        if (person != null) {
            person.setActiveActionCategoryId(categoryId);
            person.save();
        }
        return Response.ok(getRenderedStoryViewContext(coven)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{actionId}/choice")
    public Response chooseOption(@Auth Coven coven, @PathParam("actionId") long actionId, @Valid StoryChoice choice) {
        AbstractAction action = Daos.actionRepo.getAction(actionId);
        Person person = null;
        if (action == null) {
            log.error("Nonexistent actionId={} chosen, with choiceId={} and covenId={}", actionId, choice.getChoiceId(), coven.getId());
            return getBadRequestResponse(coven, action, person);
        }
        StoryInstance storyInstance = coven.getRunningStory(actionId);
        if (storyInstance == null) {
            log.error("Non-running actionId={} chosen, with choiceId={} and covenId={}", actionId, choice.getChoiceId(), coven.getId());
            return getBadRequestResponse(coven, action, person);
        }
        storyInstance.cleanBeforeNewChoice();
        if (storyInstance.getPersonId() > 0) {
            person = Daos.personDao.findById(storyInstance.getPersonId());
        }

        if (!action.isValidChoice(storyInstance, choice)) {
            log.error("Invalid choiceId={} chosen for actionId={} and covenId={}", choice.getChoiceId(), actionId, coven.getId());
            return getBadRequestResponse(coven, action, person);
        }
        action.advanceStory(Daos.functionRepo, coven, person, storyInstance, choice);
        if (person != null)
            person.save();
        coven.save();

        RenderedStoryViewContext renderedStoryViewContext;
        if (!storyInstance.isDeleted()) {
            log.info("Saving story " + storyInstance);
            Daos.storyDao.saveRunningStory(storyInstance);
            renderedStoryViewContext = getRenderedStoryViewContext(action, coven, person, storyInstance);
        } else {
            return getDeletedStoryResponse(coven, person);
        }

        return Response.ok(renderedStoryViewContext).build();
    }

    private RenderedStoryViewContext getRenderedStoryViewContext(AbstractAction action, Coven coven, Person person,
        StoryInstance storyInstance) {
        if (storyInstance == null || person == null) {
            return getRenderedStoryViewContext(coven);
        } else {
            StoryView view = action.getStoryView(storyInstance, coven, person);
            List<AbstractAction> validActions = null;
            if (view == null) {
                validActions = Daos.actionRepo.getActions(coven, person, person.getCurrentCategory());

            }
            return new RenderedStoryViewContext(action, view, coven, person, storyInstance, validActions);
        }
    }

    public RenderedStoryViewContext getRenderedStoryViewContext(Coven coven) {
        StoryView view = null;
        StoryInstance story = null;
        AbstractAction action = null;
        List<AbstractAction> validActions = null;
        Person person = null;
        long activeStoryId = -1;
        if (coven.getFocusedPersonId() > 0) {
            person = Daos.personDao.findById(coven.getFocusedPersonId());
            activeStoryId = person.getActiveStoryId();
        } else {
            activeStoryId = coven.getActiveStoryId();
        }
        try {
            if (activeStoryId >= 0) {
                long personId = person == null ? 0 : person.getId();
                story = Daos.storyDao.findRunningStory(coven.getId(), personId, activeStoryId);
                //                story = coven.getRunningStory(activeStoryId);
                action = Daos.actionRepo.getAction(activeStoryId);
                if (story == null) {
                    log.error("activeStoryId={} but no running story for covenId={}, personId={} was found", activeStoryId, coven.getId(),
                        personId);
                } else if (action == null) {
                    log.error("activeStoryId={} but no registered action was found. covenId={}, personId={}", activeStoryId, coven.getId(),
                        personId);
                } else {
                    view = action.getStoryView(story, coven, person);
                }
            }
        } catch (Throwable e) {
            log.error("Unexpected exception while getting current story", e);
        }
        if (view == null) {
            validActions = Daos.actionRepo.getActions(coven, person, person.getCurrentCategory());

        }
        return new RenderedStoryViewContext(action, view, coven, person, story, validActions);
    }

}
