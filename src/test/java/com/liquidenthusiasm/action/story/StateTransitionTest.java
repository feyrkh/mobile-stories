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
        String json = "\"1\"";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(1, st.getTarget());
        assertNull(st.getTriggers());
    }

    @Test
    public void canDeserializeSimpleInt() throws IOException {
        String json = "1";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(1, st.getTarget());
        assertNull(st.getTriggers());
    }

    @Test
    public void canDeserializeWithTriggers() throws IOException {
        String json = "\"2 if si_registerStudent<1\"";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(2, st.getTarget());
        assertNotNull(st.getTriggers());
        assertEquals("si_registerStudent", st.getTriggers()[0].getConditions()[0].getVariable());
    }

    @Test
    public void canDeserializeWithComplexTriggers() throws IOException {
        String json = "\"2 if si_registerStudent<1 & si_otherVar=32\"";
        //        String json = "{\"target\":2, \"triggers\": [\"si_registerStudent<1 & si_otherVar=32\"]}";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        assertEquals(2, st.getTarget());
        assertNotNull(st.getTriggers());
        assertEquals("si_registerStudent", st.getTriggers()[0].getConditions()[0].getVariable());
        assertEquals("si_otherVar", st.getTriggers()[0].getConditions()[1].getVariable());
    }

    @Test
    public void canDeserializeWithOredTriggers() throws IOException {
        String json = "\"2 if si_registerStudent<1 & si_otherVar=32 | si_secondTrig=1\"";
        //        String json = "{\"target\":2, \"triggers\": [\"si_registerStudent<1 & si_otherVar=32\", \"si_secondTrig = 1\"]}";
        StateTransition st = mapper.readValue(json, StateTransition.class);
        String serialized = mapper.writeValueAsString(st);
        assertEquals(json, serialized);
        assertEquals(2, st.getTarget());
        assertNotNull(st.getTriggers());
        assertEquals("si_registerStudent", st.getTriggers()[0].getConditions()[0].getVariable());
        assertEquals("si_otherVar", st.getTriggers()[0].getConditions()[1].getVariable());
        assertEquals("si_secondTrig", st.getTriggers()[1].getConditions()[0].getVariable());
    }
}