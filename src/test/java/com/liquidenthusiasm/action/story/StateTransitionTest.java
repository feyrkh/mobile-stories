package com.liquidenthusiasm.action.story;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

public class StateTransitionTest {

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
    }

    @Test
    public void canDeserializeSimple() throws IOException {
        String json = "{\"target\":1}";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(1, st.getTarget());
        assertNull(st.getTriggers());
    }

    @Test
    public void canDeserializeWithTriggers() throws IOException {
        String json = "{\"target\":2, \"triggers\": [\"si_registerStudent<1\"]}";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(2, st.getTarget());
        assertNotNull(st.getTriggers());
        assertEquals("si_registerStudent", st.getTriggers()[0].getConditions()[0].getVariable());
    }

    @Test
    public void canDeserializeWithComplexTriggers() throws IOException {
        String json = "{\"target\":2, \"triggers\": [\"si_registerStudent<1 & si_otherVar=32\"]}";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(2, st.getTarget());
        assertNotNull(st.getTriggers());
        assertEquals("si_registerStudent", st.getTriggers()[0].getConditions()[0].getVariable());
        assertEquals("si_otherVar", st.getTriggers()[0].getConditions()[1].getVariable());
    }

}