package com.liquidenthusiasm.dao;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyDaoIT extends AbstractDaoTest {

    PropertyDao dao;

    long covenId, personId, actionId;

    @Before
    public void setup() {
        Random r = new Random();
        covenId = r.nextInt();
        personId = r.nextInt();
        actionId = r.nextInt();
        dao = dbi.onDemand(PropertyDao.class);
    }

    @After
    public void clearTables() {
        resetDb();
    }

    @Test
    public void canSetIntProperty() {
        dao.updateIntProperty(covenId, personId, "myProp", 30);
        int val = dao.getIntProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo(30);
    }

    @Test
    public void canSetSameIntPropNameForMultiplePersonsInCoven() {
        for (int i = 0; i < 10; i++) {
            dao.updateIntProperty(covenId, i, "myProp", 30 + i);
        }
        for (int i = 0; i < 10; i++) {
            int val = dao.getIntProperty(covenId, i, "myProp");
            assertThat(val).isEqualTo(30 + i);
        }
    }

    @Test
    public void canSetSameIntPropNameForSamePersonsInMultipleCoven() {
        for (int i = 0; i < 10; i++) {
            dao.updateIntProperty(i, personId, "myProp", 30 + i);
        }
        for (int i = 0; i < 10; i++) {
            int val = dao.getIntProperty(i, personId, "myProp");
            assertThat(val).isEqualTo(30 + i);
        }
    }

    @Test
    public void canUpdateIntProperty() {
        dao.updateIntProperty(covenId, personId, "myProp", 30);
        int val = dao.getIntProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo(30);

        dao.updateIntProperty(covenId, personId, "myProp", 42);
        val = dao.getIntProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo(42);
    }

    @Test
    public void canDeleteIntProperty() {
        dao.updateIntProperty(covenId, personId, "myProp", 30);
        int val = dao.getIntProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo(30);

        dao.deleteIntProperty(covenId, personId, "myProp");
        val = dao.getIntProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo(0);
    }

    @Test
    public void canSetStrProperty() {
        dao.updateStrProperty(covenId, personId, "myProp", "test");
        String val = dao.getStrProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo("test");
    }

    @Test
    public void canSetSameStrPropNameForMultiplePersonsInCoven() {
        for (int i = 0; i < 10; i++) {
            dao.updateStrProperty(covenId, i, "myProp", "test" + i);
        }
        for (int i = 0; i < 10; i++) {
            String val = dao.getStrProperty(covenId, i, "myProp");
            assertThat(val).isEqualTo("test" + i);
        }
    }

    @Test
    public void canSetSameStrPropNameForSamePersonsInMultipleCoven() {
        for (int i = 0; i < 10; i++) {
            dao.updateStrProperty(i, personId, "myProp", "test" + i);
        }
        for (int i = 0; i < 10; i++) {
            String val = dao.getStrProperty(i, personId, "myProp");
            assertThat(val).isEqualTo("test" + i);
        }
    }

    @Test
    public void canUpdateStrProperty() {
        dao.updateStrProperty(covenId, personId, "myProp", "first");
        String val = dao.getStrProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo("first");

        dao.updateStrProperty(covenId, personId, "myProp", "second");
        val = dao.getStrProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo("second");
    }

    @Test
    public void canDeleteStrProperty() {
        dao.updateStrProperty(covenId, personId, "myProp", "test");
        String val = dao.getStrProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo("test");

        dao.deleteStrProperty(covenId, personId, "myProp");
        val = dao.getStrProperty(covenId, personId, "myProp");
        assertThat(val).isEqualTo(null);
    }
}
