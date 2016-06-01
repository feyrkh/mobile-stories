package com.liquidenthusiasm.resources;

import java.util.Map;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Optional;
import com.liquidenthusiasm.auth.CovenBasicAuthenticator;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.views.AccountCreatedView;
import com.liquidenthusiasm.resources.views.CreateAccountView;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.views.View;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);

    private final CovenResource covenResource;

    private final CachingAuthenticator<String, Coven> cachingAuthenticator;

    @Context private ClientRequestContext requestContext;

    private final CovenBasicAuthenticator authenticator;

    public LoginResource(CovenResource covenResource, CachingAuthenticator<String, Coven> cachingAuthenticator) {
        this.covenResource = covenResource;
        this.cachingAuthenticator = cachingAuthenticator;
        authenticator = new CovenBasicAuthenticator(Daos.covenDao);
    }

    @GET
    @Path("/logout")
    public Response logout(@Auth Coven coven, @CookieParam("loginCookie") String loginCookie) {
        cachingAuthenticator.invalidate(loginCookie);
        Daos.covenDao.login(coven.getId(), UUID.randomUUID().toString());
        return Response.ok().build();
    }

    @GET
    @Path("/account/create")
    @Produces(MediaType.TEXT_HTML)
    public CreateAccountView createAccountPage() {
        return new CreateAccountView();
    }

    @POST
    @Path("/account/create")
    @Produces(MediaType.TEXT_HTML)
    public View doCreateAccount(@FormParam("email") String email, @FormParam("displayName") String displayName,
        @FormParam("pass") String pass, @FormParam("passAgain") String passAgain) {
        CreateAccountView errorView = new CreateAccountView(email, displayName, pass, passAgain);
        if (StringUtils.isBlank(email)) {
            errorView.addError("Email account must not be blank.");
        }
        if (StringUtils.isBlank(displayName)) {
            errorView.addError("Display name must not be blank.");
        }
        if (StringUtils.isBlank(pass)) {
            errorView.addError("Password must not be blank.");
        }
        if (!pass.equals(passAgain)) {
            errorView.addError("Passwords do not match.");
        }
        if (errorView.hasErrors()) {
            return errorView;
        }
        Coven c = new Coven();
        c.setDisplayName(displayName);
        c.setName(email);
        c.setPassword(pass);
        try {
            Coven r = covenResource.create(c);
            return new AccountCreatedView(r);
        } catch (Exception e) {
            errorView.addError(e.getMessage());
        }
        return errorView;
    }

    @POST
    @Path("/login")
    @Timed
    public Response login(Map<String, String> creds) throws AuthenticationException {
        Optional<Coven> authed;
        try {
            authed = authenticator.authenticate(new BasicCredentials(creds.get("user"), creds.get("pass")));
        } catch (Exception e) {
            log.error("Exception while authenticating " + creds, e);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (authed.isPresent()) {
            String cookie = UUID.randomUUID().toString();
            Daos.covenDao.login(authed.get().getId(), cookie);
            return Response.ok().cookie(NewCookie.valueOf("loginCookie=" + cookie)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
