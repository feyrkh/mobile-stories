package com.liquidenthusiasm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.CovenResource;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class CovenCookieAuthenticator implements Authenticator<String, Coven> {

    private static final Logger log = LoggerFactory.getLogger(CovenResource.class);

    private final CovenDao covenDao;

    public CovenCookieAuthenticator(CovenDao covenDao) {
        this.covenDao = covenDao;
    }

    @Override public Optional<Coven> authenticate(String cookie) throws AuthenticationException {
        log.info("Coven authenticating with cookie={}", cookie);
        Coven coven = covenDao.findByLoginCookie(cookie);
        if (coven != null) {
            return Optional.of(coven);
        }
        log.info("Coven failed authentication with cookie={}", cookie);
        return Optional.of(new Coven());
    }
}
