package com.liquidenthusiasm.action.story;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.domain.FixtureTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StoryStateTest {

    @Before
    public void setup() {
    }

    @Test
    public void canDeserialize() {
        StoryState state = FixtureTestUtil.loadFixture("fixtures/storyState.json", StoryState.class);
        assertNotNull(state);
        StoryCallInputMapping[] option0PostSubmitCallInputs = state.getOptions()[0].getPostSubmitCalls()[0].getInputs();
        assertNotNull("expected non-null inputs", option0PostSubmitCallInputs);
        assertEquals("{{ss_name}}", option0PostSubmitCallInputs[0].getContextVarName());
        assertEquals("{{ss_focus}}", option0PostSubmitCallInputs[1].getContextVarName());
    }

}