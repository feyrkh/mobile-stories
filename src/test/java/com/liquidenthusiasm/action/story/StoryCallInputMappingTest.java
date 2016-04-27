package com.liquidenthusiasm.action.story;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StoryCallInputMappingTest {

    @Before
    public void setup() {
    }

    @Test
    public void canCreateMappingFromInputStr() {
        StoryCallInputMapping input = StoryCallInputMapping.from("ss_name->studentName");
        assertEquals("ss_name", input.getContextVarName());
        assertEquals("studentName", input.getFunctionVarName());
    }

    @Test
    public void canCreateMappingFromInputStrWithSpaces() {
        StoryCallInputMapping input = StoryCallInputMapping.from("ss_focus -> studentFocus");

        assertEquals("ss_focus", input.getContextVarName());
        assertEquals("studentFocus", input.getFunctionVarName());
    }

}