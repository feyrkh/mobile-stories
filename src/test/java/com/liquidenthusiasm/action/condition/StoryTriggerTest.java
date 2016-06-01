package com.liquidenthusiasm.action.condition;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StoryTriggerTest {

    Coven coven;

    Person person;

    Map<String, Object> state;

    StoryTrigger st;

    @Before
    public void setup() {
        st = new StoryTrigger();
        coven = mock(Coven.class);
        when(coven.getIntProperty("ci_v")).thenReturn(1);
        when(coven.getStrProperty("cs_v")).thenReturn("valueC");
        person = mock(Person.class);
        when(person.getIntProperty("pi_v")).thenReturn(2);
        when(person.getStrProperty("ps_v")).thenReturn("valueP");
        state = new HashMap<>();
        state.put("si_v", 3);
        state.put("ss_v", "valueS");
    }

    @Test
    public void canDeserialize() throws IOException {
        String json = "{\"conditions\":[\"ci_living_space>0\",\"ci_members<=0\"]}";
        ObjectMapper mapper = new ObjectMapper();
        StoryTrigger deser = mapper.readValue(json, StoryTrigger.class);
        assertNotNull(deser);
        TriggerCondition[] triggers = deser.getConditions();
        assertEquals(2, triggers.length);
        assertEquals("ci_living_space", triggers[0].getVariable());
        assertEquals(TriggerConditionOperator.GT, triggers[0].getOp());
        assertEquals(0, triggers[0].getIntValue());
        assertEquals(null, triggers[0].getStrValue());
        assertEquals("ci_members", triggers[1].getVariable());
        assertEquals(TriggerConditionOperator.LTE, triggers[1].getOp());
        assertEquals(0, triggers[1].getIntValue());
        assertEquals(null, triggers[1].getStrValue());

        String serialized = mapper.writeValueAsString(deser);
        assertEquals(serialized, json);
    }

    @Test
    public void emptyStoryTriggerIsAlwaysTriggered() {
        assertTrue(st.isTriggered(null, null, null));
    }

    @Test
    public void canTriggerOnMissingVar() {
        st = StoryTrigger.fromString("ci_missingVar=0");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnCovenInt() {
        st = StoryTrigger.fromString("ci_v=1");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canAvoidTriggerOnCovenInt() {
        st = StoryTrigger.fromString("ci_v=0");
        assertFalse(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnStateInt() {
        st = StoryTrigger.fromString("si_v<4");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canAvoidTriggerOnStateInt() {
        st = StoryTrigger.fromString("si_v!=3");
        assertFalse(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnPlayerInt() {
        st = StoryTrigger.fromString("pi_v>=2");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canAvoidTriggerOnPlayerInt() {
        st = StoryTrigger.fromString("pi_v<2");
        assertFalse(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnCovenStr() {
        st = StoryTrigger.fromString("cs_v=valueC");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canAvoidTriggerOnCovenStr() {
        st = StoryTrigger.fromString("cs_v=value");
        assertFalse(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnStateStr() {
        st = StoryTrigger.fromString("ss_v=valueS");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canAvoidTriggerOnStateStr() {
        st = StoryTrigger.fromString("ss_v=values");
        assertFalse(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnPlayerStr() {
        st = StoryTrigger.fromString("ps_v=valueP");
        assertTrue(st.isTriggered(coven, person, state));
    }

    @Test
    public void canAvoidTriggerOnPlayerStr() {
        st = StoryTrigger.fromString("ps_v!=valueP");
        assertFalse(st.isTriggered(coven, person, state));
    }

    @Test
    public void canTriggerOnMultipleAnds() {
        st = StoryTrigger.fromString("ps_v=valueP & ss_v = valueS & ci_v= 1");
        assertTrue(st.isTriggered(coven, person, state));
        assertEquals("ps_v=valueP & ss_v=valueS & ci_v=1", st.toString());
    }

    @Test
    public void canAvoidTriggerOnMultipleAnds() {
        st = StoryTrigger.fromString("ps_v!=valueP & ss_v = valueS & ci_v= 1");
        assertFalse("first fail", st.isTriggered(coven, person, state));
        st = StoryTrigger.fromString("ps_v=valueP & ss_v != valueS & ci_v= 1");
        assertFalse("second fail", st.isTriggered(coven, person, state));
        st = StoryTrigger.fromString("ps_v=valueP & ss_v = valueS & ci_v!= 1");
        assertFalse("third fail", st.isTriggered(coven, person, state));
        st = StoryTrigger.fromString("ps_v!=valueP & ss_v = valueS & ci_v!= 1");
        assertFalse("first, third fail", st.isTriggered(coven, person, state));
        st = StoryTrigger.fromString("ps_v!=valueP & ss_v != valueS & ci_v!= 1");
        assertFalse("all fail", st.isTriggered(coven, person, state));
    }

}