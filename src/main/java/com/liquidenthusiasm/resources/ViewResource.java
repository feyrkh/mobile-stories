package com.liquidenthusiasm.resources;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.story.StoryChoice;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.views.GameView;
import com.liquidenthusiasm.resources.views.HomeView;
import com.liquidenthusiasm.resources.views.RenderedStoryView;
import com.liquidenthusiasm.resources.views.RenderedStoryViewContext;
import io.dropwizard.auth.Auth;
import io.dropwizard.views.View;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class ViewResource {

    private static final Logger log = LoggerFactory.getLogger(ViewResource.class);

    private final AvailableActionsResource availableActions;

    public ViewResource(AvailableActionsResource availableActions) {
        this.availableActions = availableActions;
    }

    @GET
    public HomeView home() {
        return new HomeView();
    }

    @GET
    @Path("game")
    public View game(@Auth Coven coven) {
        if (coven.getId() > 0)
            return new GameView(coven);
        else
            return new HomeView();
    }

    @GET
    @Path("currentStory")
    public RenderedStoryView getCurrentStory(@Auth Coven coven) {
        RenderedStoryViewContext ctx = availableActions.getRenderedStoryViewContext(coven);
        return new RenderedStoryView(ctx);
    }

    @GET
    @Path("sidebar")
    public RenderedStoryView getCurrentSidebar(@Auth Coven coven) {
        RenderedStoryViewContext ctx = availableActions.getRenderedStoryViewContext(coven);
        return new RenderedStoryView(ctx);
    }

    @GET
    @Path("header")
    public RenderedStoryView getCurrentHeader(@Auth Coven coven) {
        RenderedStoryViewContext ctx = availableActions.getRenderedStoryViewContext(coven);
        return new RenderedStoryView(ctx);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("choice/{actionId}")
    public RenderedStoryView chooseOption(@Auth Coven coven, @PathParam("actionId") long actionId, @Valid StoryChoice choice) {
        Response r = availableActions.chooseOption(coven, actionId, choice);
        RenderedStoryViewContext ctx = (RenderedStoryViewContext) r.getEntity();
        return new RenderedStoryView(ctx);
    }

    @POST
    @Path("action/{actionId}")
    public RenderedStoryView performAction(@Auth Coven coven, @PathParam("actionId") long actionId) {
        Response r = availableActions.performAction(coven, actionId);
        RenderedStoryViewContext ctx = (RenderedStoryViewContext) r.getEntity();
        return new RenderedStoryView(ctx);
    }

    @POST
    @Path("action/travel/{categoryId}")
    public RenderedStoryView travel(@Auth Coven coven, @PathParam("categoryId") long categoryId) {
        Response r = availableActions.travel(coven, categoryId);
        RenderedStoryViewContext ctx = (RenderedStoryViewContext) r.getEntity();
        return new RenderedStoryView(ctx);
    }
}
