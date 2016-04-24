package com.liquidenthusiasm.resources;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.AbstractAction;
import com.liquidenthusiasm.action.ActionCategory;
import com.liquidenthusiasm.action.ActionRepo;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.StoryView;
import io.dropwizard.auth.Auth;

@Path("/actions")
@Produces(MediaType.APPLICATION_JSON)
public class AvailableActionsResource {

    private static final Logger log = LoggerFactory.getLogger(AvailableActionsResource.class);

    private final ActionRepo actionRepo;

    public AvailableActionsResource(ActionRepo actionRepo) {
        this.actionRepo = actionRepo;
    }

    @GET
    @Path("coven/{category}")
    public List<AbstractAction> getCovenActions(@Auth Coven coven, @PathParam("category") ActionCategory category) {
        return actionRepo.getCovenActions(coven, category);
    }

    @POST
    @Path("{actionId}/perform")
    public Response performAction(@Auth Coven coven, @PathParam("actionId") long actionId) {
        AbstractAction action = actionRepo.getCovenAction(actionId);
        if (action == null) {
            log.error("Nonexistent actionId={} chosen, with covenId={}", actionId, coven.getId());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if (!action.canStartStory(coven, null)) {
            log.error("actionId={} chosen which isn't valid for covenId={}", actionId, coven.getId());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        StoryInstance storyInstance = action.getOrGenerateStoryInstance(coven);
        StoryView storyView = action.getStoryView(storyInstance);

        return Response.ok(storyView).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{actionId}/choice")
    public Response chooseOption(@Auth Coven coven, @PathParam("actionId") long actionId, @Valid StoryChoice choice) {
        AbstractAction action = actionRepo.getCovenAction(actionId);
        if (action == null) {
            log.error("Nonexistent actionId={} chosen, with choiceId={} and covenId={}", actionId, choice.getChoiceId(), coven.getId());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        StoryInstance storyInstance = coven.getRunningStory(actionId);
        if (storyInstance == null) {
            log.error("Non-running actionId={} chosen, with choiceId={} and covenId={}", actionId, choice.getChoiceId(), coven.getId());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (!action.isValidChoice(storyInstance, choice)) {
            log.error("Invalid choiceId={} chosen for actionId={} and covenId={}", choice.getChoiceId(), actionId, coven.getId());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        action.advanceStory(coven, storyInstance, choice);
        StoryView storyView = action.getStoryView(storyInstance);
        return Response.ok(storyView).build();
    }
}
