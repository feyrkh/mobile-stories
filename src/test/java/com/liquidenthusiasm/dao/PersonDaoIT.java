package com.liquidenthusiasm.dao;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.domain.FixtureTestUtil;
import com.liquidenthusiasm.domain.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;

public class PersonDaoIT extends AbstractDaoTest {

    PersonDao dao;

    Person p;

    @Before
    public void setup() {
        dao = dbi.onDemand(PersonDao.class);
        p = FixtureTestUtil.loadFixture("fixtures/person.json", Person.class);
    }

    @Test
    public void canSave() {
        long id = dao.insert(p);
        assertNotEquals(0, id);
    }

    @Test
    public void saveLoadSerializesCorrectly() {
        long id = dao.insert(p);
        p.setId(id);
        Person retrieved = dao.findById(id);
        assertEqualPerson(retrieved, p);
    }

    private void assertEqualPerson(Person actual, Person expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected);
    }

    @Test
    public void canFindById() {
        long id = dao.insert(p);
        p.setId(id);
        Person retrieved = dao.findById(id);
        assertEqualPerson(retrieved, p);
        retrieved = dao.findById(id + 1);
        assertThat(retrieved).isNull();
    }

}