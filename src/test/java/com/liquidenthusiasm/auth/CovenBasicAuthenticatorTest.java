package com.liquidenthusiasm.auth;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.dao.MockCovenDao;
import com.liquidenthusiasm.domain.Coven;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;

import static org.assertj.core.api.Assertions.assertThat;

public class CovenBasicAuthenticatorTest {

    CovenBasicAuthenticator auth;

    CovenDao covenDao;

    private Coven user1;

    private Coven user2;

    @Before
    public void setup() {
        covenDao = MockCovenDao.buildMock();
        auth = new CovenBasicAuthenticator(covenDao);
        user1 = MockCovenDao.addMockCovenToDao(covenDao, 1, "user1");
        user2 = MockCovenDao.addMockCovenToDao(covenDao, 2, "user2");
    }

    @Test
    public void authHappyPathWorks() throws AuthenticationException {
        Optional<Coven> result = auth.authenticate(new BasicCredentials("user1", "user1_password"));
        assertThat(result.get()).isEqualTo(user1);

        result = auth.authenticate(new BasicCredentials("user2", "user2_password"));
        assertThat(result.get()).isEqualTo(user2);
    }

    @Test
    public void invalidPasswordFails() throws AuthenticationException {
        Optional<Coven> result = auth.authenticate(new BasicCredentials("user1", "user2_password"));
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void invalidUsernameFails() throws AuthenticationException {
        Optional<Coven> result = auth.authenticate(new BasicCredentials("user3", "user3_password"));
        assertThat(result.isPresent()).isFalse();
    }
}