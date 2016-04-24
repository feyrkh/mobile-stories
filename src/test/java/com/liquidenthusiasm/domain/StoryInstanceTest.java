package com.liquidenthusiasm.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoryInstanceTest {

    @Before
    public void setup() {
    }

    @Test
    public void canSerializeAndDeserializeState() throws IOException {
        StoryInstance s1 = new StoryInstance();
        s1.setCovenId(2);
        s1.setActionId(1);
        StoryInstance s2 = new StoryInstance();

        Map<String, Object> startingState = new HashMap<>();
        Map<String, Object> nestedState = new HashMap<>();
        nestedState.put("secondLevel", "TWO");
        nestedState.put("float", 2.12d);
        startingState.put("topLevel", 1);
        startingState.put("nested", nestedState);

        s1.setState(startingState);
        s2.setStateJson(s1.getStateJson());

        assertEquals("state json", s1.getStateJson(), s2.getStateJson());
        assertEquals(2.12d, (double) ((Map<String, Object>) s1.getState().get("nested")).get("float"), 0.00001d);
        assertEquals(2.12d, (double) ((Map<String, Object>) s2.getState().get("nested")).get("float"), 0.00001d);

        String s1Json = StoryInstance.MAPPER.writeValueAsString(s1);
        System.out.println(s1Json);
        s2 = StoryInstance.MAPPER.readValue(s1Json, StoryInstance.class);
        String s2Json = StoryInstance.MAPPER.writeValueAsString(s2);

        assertEquals(s1Json, s2Json);
    }

    @Test
    public void invalidStateJsonReturnsEmptyMap() {
        StoryInstance s1 = new StoryInstance();
        s1.setStateJson("{\"test\":1");
        assertEquals("{}", s1.getStateJson());
        assertEquals(0, s1.getState().size());
    }

    private static class Exploder {

        public String getField() {
            throw new RuntimeException();
        }
    }

    @Test
    public void unserializableStateGivesEmptyStateJson() {
        StoryInstance s1 = new StoryInstance();
        s1.getState().put("explode", new Exploder());
        String stateJson = s1.getStateJson();
        assertEquals("{}", stateJson);
    }

}