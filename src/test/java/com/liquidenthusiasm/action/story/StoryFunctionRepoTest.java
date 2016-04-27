package com.liquidenthusiasm.action.story;

import org.junit.Before;
import org.junit.Test;

import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StoryFunctionRepoTest {

    StoryFunctionRepo repo;

    private Coven coven;

    private Person person;

    private StoryInstance story;

    private StoryFunction simple = (args, coven, person, story) -> {
        story.state("calledSimple", true);
    };

    private StoryFunction args = (args, coven, person, story) -> {
        StringBuilder retval = new StringBuilder();
        retval.append("storyStr:").append(args.get("storyStr")).append("\n");
        retval.append("storyInt:").append(args.get("storyInt")).append("\n");
        retval.append("covenStr:").append(args.get("covenStr")).append("\n");
        retval.append("covenInt:").append(args.get("covenInt")).append("\n");
        retval.append("personStr:").append(args.get("personStr")).append("\n");
        retval.append("personInt:").append(args.get("personInt")).append("\n");
        coven.setStrProperty("argsRetval", retval.toString());
    };

    @Before
    public void setup() {
        repo = new StoryFunctionRepo();
        coven = mock(Coven.class);
        person = mock(Person.class);
        story = new StoryInstance();
        repo.put("simple", simple);
        repo.put("args", args);
    }

    @Test
    public void canCallSimpleFunction() {
        assertEquals(null, story.state("calledSimple"));
        repo.call("simple", null, coven, person, story);
        assertEquals(true, story.state("calledSimple"));
    }

    @Test
    public void canCallFunctionWithArgs() {
        int storyInt = -10;
        int covenInt = 10;
        int personInt = 999;
        StoryCallInputMapping[] inputs = new StoryCallInputMapping[6];
        inputs[0] = StoryCallInputMapping.from("{{ss_StoryStr}}->storyStr");
        inputs[1] = StoryCallInputMapping.from("{{si_StoryInt}}->storyInt");
        inputs[2] = StoryCallInputMapping.from("{{cs_CovenStr}}->covenStr");
        inputs[3] = StoryCallInputMapping.from("{{ci_CovenInt}}->covenInt");
        inputs[4] = StoryCallInputMapping.from("{{ps_PersonStr}}->personStr");
        inputs[5] = StoryCallInputMapping.from("{{pi_PersonInt}}->personInt");

        story.state("ss_StoryStr", "STORY_STR");
        story.state("si_StoryInt", storyInt);
        when(coven.getStrProperty("cs_CovenStr")).thenReturn("COVEN_STR");
        when(coven.getIntProperty("ci_CovenInt")).thenReturn(covenInt);
        when(person.getIntProperty("pi_PersonInt")).thenReturn(personInt);
        when(person.getStrProperty("ps_PersonStr")).thenReturn("PERSON_STR");
        repo.call("args", inputs, coven, person, story);

        String expectedArgsVal = new StringBuilder()
            .append("storyStr:").append("STORY_STR").append("\n")
            .append("storyInt:").append(storyInt).append("\n")
            .append("covenStr:").append("COVEN_STR").append("\n")
            .append("covenInt:").append(covenInt).append("\n")
            .append("personStr:").append("PERSON_STR").append("\n")
            .append("personInt:").append(personInt).append("\n")
            .toString();

        verify(coven).setStrProperty("argsRetval", expectedArgsVal);
    }
}