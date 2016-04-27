package com.liquidenthusiasm.action.story;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;

public class StoryCallTest {

    ObjectMapper mapper = new ObjectMapper();

    StoryCall sc;

    @Before
    public void setup() {
        sc = new StoryCall();
    }

    @Test
    public void simpleStoryCall() throws IOException {
        sc.setFunction("simple");
        checkSerde(sc);
    }

    @Test
    public void storyCallWithInputs() throws IOException {
        sc.setFunction("withInputs");
        sc.setInputs(new String[] { "1->one" });
        StoryCall deser = checkSerde(sc);
        assertEquals("1", deser.getInputs()[0].getContextVarName());
        assertEquals("one", deser.getInputs()[0].getFunctionVarName());
    }

    private StoryCall checkSerde(StoryCall sc) throws IOException {
        String serializedOnce = mapper.writeValueAsString(sc);
        StoryCall deser = mapper.readValue(serializedOnce, StoryCall.class);
        String serializedTwice = mapper.writeValueAsString(deser);
        assertEquals(serializedTwice, serializedOnce);
        System.out.println("Serialized: " + serializedOnce);
        return deser;
    }

}