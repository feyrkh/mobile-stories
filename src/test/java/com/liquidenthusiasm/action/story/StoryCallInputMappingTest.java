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
        StoryCallInputMapping input = StoryCallInputMapping.from("studentName=ss_name");
        assertEquals("studentName", input.getFunctionVarName());
        assertEquals("ss_name", input.getContextVarName());
    }

    @Test
    public void canCreateMappingFromInputStrWithSpaces() {
        StoryCallInputMapping input = StoryCallInputMapping.from(" studentFocus = ss_focus ");

        assertEquals("studentFocus", input.getFunctionVarName());
        assertEquals("ss_focus", input.getContextVarName());
    }

    @Test
    public void canHaveValuesWithEqualSigns() {
        StoryCallInputMapping input = StoryCallInputMapping.from("equality=ss_name=Kevin");
        assertEquals("equality", input.getFunctionVarName());
        assertEquals("ss_name=Kevin", input.getContextVarName());

    }

}