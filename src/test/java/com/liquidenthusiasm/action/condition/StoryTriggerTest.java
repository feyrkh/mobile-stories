package com.liquidenthusiasm.action.condition;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StoryTriggerTest {

    @Before
    public void setup() {
    }

    @Test
    public void canDeserialize() throws IOException {
        String json = "{\"conditions\":\"ci_Living Space > 0 & ci_Members <= 0\"}";
        ObjectMapper mapper = new ObjectMapper();
        StoryTrigger deser = mapper.readValue(json, StoryTrigger.class);
        assertNotNull(deser);
        TriggerCondition[] triggers = deser.getConditions();
        assertEquals(2, triggers.length);
        assertEquals("ci_Living Space", triggers[0].getVariable());
        assertEquals(TriggerConditionOperator.GT, triggers[0].getOp());
        assertEquals(0, triggers[0].getIntValue());
        assertEquals(null, triggers[0].getStrValue());
        assertEquals("ci_Members", triggers[1].getVariable());
        assertEquals(TriggerConditionOperator.LTE, triggers[1].getOp());
        assertEquals(0, triggers[1].getIntValue());
        assertEquals(null, triggers[1].getStrValue());
    }

}