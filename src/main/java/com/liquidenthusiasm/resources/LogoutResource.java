package com.liquidenthusiasm.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import com.sun.xml.internal.ws.client.RequestContext;
import io.dropwizard.auth.Auth;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON)
public class LogoutResource {

    private static final Logger log = LoggerFactory.getLogger(LogoutResource.class);

    @Context private RequestContext requestContext;

    @Path("/")
    @GET
    public Response logout(@Auth Coven coven) {
        return Response.status(UNAUTHORIZED).build();
    }
}
