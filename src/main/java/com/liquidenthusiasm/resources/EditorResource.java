package com.liquidenthusiasm.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.views.StoryEditorView;
import io.dropwizard.auth.Auth;

@Path("/editor")
@Produces(MediaType.TEXT_HTML)
public class EditorResource {

    private static final Logger log = LoggerFactory.getLogger(EditorResource.class);

    public EditorResource() {
    }

    @GET
    @Path("/")
    public StoryEditorView viewEditor(@Auth Coven coven) {
        //        if (!coven.isAdmin())
        //            throw new NotAuthorizedException("Get outta here.");
        return new StoryEditorView();
    }

}
