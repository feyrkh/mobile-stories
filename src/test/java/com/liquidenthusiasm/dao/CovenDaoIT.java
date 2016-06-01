package com.liquidenthusiasm.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.FixtureTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class CovenDaoIT extends AbstractDaoTest {

    CovenDao dao;

    Coven c;

    long covenId, personId, actionId;

    @Before
    public void setup() {
        covenId = 5;
        personId = 1;
        actionId = 42;
        dao = dbi.onDemand(CovenDao.class);
        Daos.covenDao = dao;
        c = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
    }

    @After
    public void clearTables() {
        resetDb();
    }

    @Test
    public void canSave() {
        long id = dao.insert(c);
        assertEquals(1, id);
    }

    @Test(expected = UnableToExecuteStatementException.class)
    public void canNotHaveDuplicateNames() {
        dao.insert(c);
        dao.insert(c);
    }

    @Test
    public void saveLoadSerializesCorrectly() {
        long id = dao.insert(c);
        c.setId(id);
        Coven retrieved = dao.findById(id);
        assertEqualCovens(retrieved, c);
    }

    private void assertEqualCovens(Coven actual, Coven expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected);
    }

    @Test
    public void canFindById() {
        long id = dao.insert(c);
        c.setId(id);
        Coven retrieved = dao.findById(id);
        assertEqualCovens(retrieved, c);
        retrieved = dao.findById(id + 1);
        assertThat(retrieved).isNull();
    }

    @Test
    public void canFindByName() {
        long id = dao.insert(c);
        c.setId(id);
        Coven retrieved = dao.findByName(c.getName());
        assertEqualCovens(retrieved, c);
        retrieved = dao.findByName("no name mcgee");
        assertThat(retrieved).isNull();
    }

    @Test
    public void canLoginAndFetchCoven() {
        long id = dao.insert(c);
        c.setId(id);
        dao.login(id, "MyLoginCookie");
        Coven retrieved = dao.findByLoginCookie("MyLoginCookie");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(id);
    }

    @Test
    public void canLoginAndFetchTwoCovens() {
        Coven c2 = FixtureTestUtil.loadFixture("fixtures/coven.json", Coven.class);
        c2.setPassword("passwordAgain");
        c2.setName("newName@gmail.com");
        long id = dao.insert(c);
        long id2 = dao.insert(c2);
        c.setId(id);
        c2.setId(id2);
        dao.login(id, "MyLoginCookie");
        dao.login(id2, "MyLoginCookie2");
        Coven retrieved = dao.findByLoginCookie("MyLoginCookie");
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(id);
        Coven retrieved2 = dao.findByLoginCookie("MyLoginCookie2");
        assertThat(retrieved2).isNotNull();
        assertThat(retrieved2.getId()).isEqualTo(id2);
    }

    @Test
    public void canFailToFetchLoggedInCoven() {
        long id = dao.insert(c);
        c.setId(id);
        dao.login(id, "MyLoginCookie");
        Coven retrieved = dao.findByLoginCookie("MyLoginCookie2");
        assertThat(retrieved).isNull();
    }
}
