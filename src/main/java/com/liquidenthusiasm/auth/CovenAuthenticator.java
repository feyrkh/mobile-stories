package com.liquidenthusiasm.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.liquidenthusiasm.BCryptUtil;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.resources.CovenResource;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class CovenAuthenticator implements Authenticator<BasicCredentials, Coven> {

    private static final Logger log = LoggerFactory.getLogger(CovenResource.class);

    private final CovenDao covenDao;

    public CovenAuthenticator(CovenDao covenDao) {
        this.covenDao = covenDao;
    }

    @Override public Optional<Coven> authenticate(BasicCredentials creds) throws AuthenticationException {
        log.info("Coven authenticating with username={}, pass={}", creds.getUsername(), creds.getPassword());
        Coven coven = covenDao.findByName(creds.getUsername());
        if (coven != null) {
            if (BCryptUtil.checkpw(creds.getPassword(), coven.getPassword())) {
                log.info("Coven authenticated with username={}, id={}", creds.getUsername(), coven.getId());
                return Optional.of(coven);
            }
        }
        log.info("Coven failed authentication with username={}", creds.getUsername());
        return Optional.absent();
    }
}
