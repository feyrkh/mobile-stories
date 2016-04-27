package com.liquidenthusiasm.action.condition;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;

public class TriggerConditionTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() {
    }

    @Test
    public void canSerde() throws IOException {
        String serialized = "\"var<=val\"";
        TriggerCondition deser = mapper.readValue(serialized, TriggerCondition.class);
        String serializedAgain = mapper.writeValueAsString(deser);
        assertEquals("serialized", serializedAgain, serialized);
    }

}