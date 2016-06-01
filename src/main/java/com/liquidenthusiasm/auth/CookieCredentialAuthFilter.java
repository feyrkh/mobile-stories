package com.liquidenthusiasm.auth;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.CachingAuthenticator;

public class CookieCredentialAuthFilter<T extends Principal> extends AuthFilter<String, T> {

    private static final Logger log = LoggerFactory.getLogger(CookieCredentialAuthFilter.class);

    private final String cookieName;

    public CookieCredentialAuthFilter(String cookieName, CachingAuthenticator<String, T> cachingAuthenticator) {
        this.cookieName = cookieName;
        this.authenticator = cachingAuthenticator;
    }

    @Override public void filter(ContainerRequestContext req) throws IOException {
        Cookie cookie = req.getCookies().get(cookieName);
        if (cookie != null) {
            try {
                final Optional e1 = this.authenticator.authenticate(cookie.getValue());
                if (e1.isPresent()) {
                    req.setSecurityContext(new SecurityContext() {

                        public Principal getUserPrincipal() {
                            return (Principal) e1.get();
                        }

                        public boolean isUserInRole(String role) {
                            return authorizer.authorize((T) e1.get(), role);
                        }

                        public boolean isSecure() {
                            return req.getSecurityContext().isSecure();
                        }

                        public String getAuthenticationScheme() {
                            return "TOKEN";
                        }
                    });
                    return;
                }
            } catch (AuthenticationException var11) {
                log.warn("Error authenticating credentials", var11);
                throw new InternalServerErrorException();
            }
        }
    }
}
