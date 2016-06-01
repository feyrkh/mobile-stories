package com.liquidenthusiasm.action.vars;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.util.InvalidVariableName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VarRepoTest {

    VarRepo vr;

    @Before
    public void setup() {
        vr = new VarRepo();
        Daos.varRepo = vr;
        c = mock(Coven.class);
        when(c.getIntProperty("ci_v")).thenReturn(CI);
        when(c.getStrProperty("cs_v")).thenReturn(CS);
        p = mock(Person.class);
        when(p.getIntProperty("pi_v")).thenReturn(PI);
        when(p.getStrProperty("ps_v")).thenReturn(PS);
        s = new HashMap<>();
        s.put("si_v", SI);
        s.put("ss_v", SS);
        si = mock(StoryInstance.class);
        when(si.getState()).thenReturn(s);

    }

    @Test
    public void canLoadVars() throws IOException, ValidationException {
        assertEquals(0, vr.getVars().size());
        vr.loadVars("fixtures/varRepo.json");
        assertEquals(3, vr.getVars().size());
    }

    @Test(expected = ValidationException.class)
    public void loadingMultipleCopiesOfVarsExplodes() throws IOException, ValidationException {
        assertEquals(0, vr.getVars().size());
        vr.loadVars("fixtures/varRepo.json");
        vr.loadVars("fixtures/varRepo.json");
    }

    private static final java.lang.Integer CI = 100;

    private static final java.lang.Integer PI = 101;

    private static final Integer SI = 102;

    private static final java.lang.String CS = "CS";

    private static final java.lang.String PS = "PS";

    private static final java.lang.String SS = "SS";

    Coven c;

    Person p;

    Map<String, Object> s;

    private StoryInstance si;

    @Test
    public void canHandleLongMultilineString() {
        String str = "Having sent off the letter, you're quite sure {{ss_name}} will be on the next coach out.\n"
            + "\n"
            + "Hopefully {{ss_pronoun_she}} will be of some help with all of the {{ss_focus}} needed.";
        String expected = "Having sent off the letter, you're quite sure Kevin will be on the next coach out.\n"
            + "\n"
            + "Hopefully he will be of some help with all of the experimentation needed.";

        s.put("ss_name", "Kevin");
        s.put("ss_pronoun_she", "he");
        s.put("ss_focus", "experimentation");
        String result = vr.interpolate(str, c, p, si);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void covenString() {
        assertEquals(CS, Daos.varRepo.getProperty("cs_v", c, p, s));
    }

    @Test
    public void covenInt() {
        assertEquals(CI, Daos.varRepo.getProperty("ci_v", c, p, s));
    }

    @Test(expected = InvalidVariableName.class)
    public void covenInvalid() {
        Daos.varRepo.getProperty("c?_v", c, p, s);
    }

    @Test
    public void personString() {
        assertEquals(PS, Daos.varRepo.getProperty("ps_v", c, p, s));
    }

    @Test
    public void personInt() {
        assertEquals(PI, Daos.varRepo.getProperty("pi_v", c, p, s));
    }

    @Test(expected = InvalidVariableName.class)
    public void personInvalid() {
        Daos.varRepo.getProperty("p?_v", c, p, s);
    }

    @Test
    public void stateString() {
        assertEquals(SS, Daos.varRepo.getProperty("ss_v", c, p, s));
    }

    @Test
    public void stateInt() {
        assertEquals(SI, Daos.varRepo.getProperty("si_v", c, p, s));
    }

    @Test(expected = InvalidVariableName.class)
    public void stateInvalid() {
        Daos.varRepo.getProperty("s?_v", c, p, s);
    }

    @Test(expected = InvalidVariableName.class)
    public void invalidString() {
        Daos.varRepo.getProperty("?s_v", c, p, s);
    }

    @Test(expected = InvalidVariableName.class)
    public void invalidInt() {
        Daos.varRepo.getProperty("?i_v", c, p, s);
    }

    @Test(expected = InvalidVariableName.class)
    public void invalidInvalid() {
        Daos.varRepo.getProperty("??_v", c, p, s);
    }
}