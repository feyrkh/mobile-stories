package com.liquidenthusiasm.resources;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.BCryptUtil;
import com.liquidenthusiasm.dao.CovenDao;
import com.liquidenthusiasm.dao.MockCovenDao;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.FixtureTestUtil;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CovenResourceTest {

    private CovenResource rsrc;

    private CovenDao covenDao;

    private Coven coven1;

    private Coven coven2;

    @Before
    public void setup() {
        BCryptUtil.workFactor = 6; // Speed up password encryption for tests
        covenDao = MockCovenDao.buildMock();
        coven1 = MockCovenDao.addMockCovenToDao(covenDao, 1, "coven1");
        coven2 = MockCovenDao.addMockCovenToDao(covenDao, 2, "coven2");
        rsrc = new CovenResource(covenDao);
    }

    @Test
    public void retrieveAllSanitizesCovens() {
        List<Coven> list = rsrc.getAll();
        assertEquals(2, list.size());
        for (Coven c : list) {
            verify(c).sanitize();
        }
    }

    @Test(expected = BadRequestException.class)
    public void creatingCovenRejectsExistingNames() throws IOException {
        Coven newCoven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        newCoven.setName(coven1.getName());
        rsrc.create(newCoven);
    }

    @Test
    public void creatingCovenWorks() {
        Coven newCoven = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        assertEquals("unknown", newCoven.getPassword());
        Coven createdCoven = rsrc.create(newCoven);
        // Password encrypted
        assertNotEquals("unknown", createdCoven.getPassword());
        assertTrue(BCryptUtil.checkpw("unknown", createdCoven.getPassword()));
        // ID set
        assertNotEquals(0, newCoven.getId());
        // Is inserted into the DB
        verify(covenDao).insert(newCoven);
    }

    @Test(expected = ForbiddenException.class)
    public void getSingleCovenRejectsIfNotAuthedToSameCoven() {
        rsrc.get(coven1, 2);
    }

    @Test
    public void getSingleCovenWorksIfAuthedToSameCoven() {
        rsrc.get(coven1, 1);
    }

    @Test
    public void getSingleCovenSanitizedIfNotAdmin() {
        Coven c = rsrc.get(coven1, 1);
        verify(c).sanitize();
    }

    @Test
    public void getSingleCovenNotSanitizedIfAdmin() {
        when(coven1.isAdmin()).thenReturn(true);
        Coven c = rsrc.get(coven1, 1);
        verify(c, never()).sanitize();
    }
}