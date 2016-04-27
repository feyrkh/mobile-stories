package com.liquidenthusiasm.domain;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.util.VariableLookup;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StoryViewTest {

    StoryView view;

    private Coven coven;

    private Person person;

    private StoryInstance story;

    @Before
    public void setup() {
        view = new StoryView();
        coven = mock(Coven.class);
        person = mock(Person.class);
        story = mock(StoryInstance.class);
    }

    @Test
    public void canInterpolateString() {
        String original =
            "{{ss_name}} ({{si_nameInt}}) has been a {{cs_profession}} ({{ci_professionInt}}) for {{pi_age}} years. {{ps_ha}} {{ps_ha}} {{ps_ha}}!";
        setupVals("Kevin", 11, "IT guy", 9, 35, "ha");
        String newStr = VariableLookup.interpolate(original, coven, person, story);
        assertEquals("Kevin (11) has been a IT guy (9) for 35 years. ha ha ha!", newStr);

        setupVals("Steph", 12, "IT girl", 10, 32, "la");
        newStr = VariableLookup.interpolate(original, coven, person, story);
        assertEquals("Steph (12) has been a IT girl (10) for 32 years. la la la!", newStr);
    }

    private void setupVals(String ss_name, int si_nameInt, String cs_profession, int ci_professionInt, int pi_age, String ps_ha) {
        when(story.state("ss_name")).thenReturn(ss_name);
        when(story.state("si_nameInt")).thenReturn(si_nameInt);
        Map<String, Object> storyState = new HashMap<>();
        storyState.put("ss_name", ss_name);
        storyState.put("si_nameInt", si_nameInt);
        when(story.getState()).thenReturn(storyState);

        when(coven.getStrProperty("cs_profession")).thenReturn(cs_profession);
        when(coven.getIntProperty("ci_professionInt")).thenReturn(ci_professionInt);
        when(person.getStrProperty("ps_ha")).thenReturn(ps_ha);
        when(person.getIntProperty("pi_age")).thenReturn(pi_age);
    }

}