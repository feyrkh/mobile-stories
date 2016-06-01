package com.liquidenthusiasm.action.function;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.liquidenthusiasm.action.story.StoryFunction;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.ValueChange;

public class RegisterStudentFunc implements StoryFunction, FunctionWithInputs, FunctionWithOutputs {

    private static final Logger log = LoggerFactory.getLogger(RegisterStudentFunc.class);

    public static final Set<String> REQUIRED_INPUT = Sets.newHashSet("studentName", "studentPronounShe", "studentFocus");

    public static final Set<String> OUTPUTS = Sets.newHashSet("se_registerStudent");

    @Override public void call(Map<String, Object> args, Coven coven, Person person, StoryInstance story) {
        story.addFlash("Recruited {{ss_name}} to the coven!");
        story.valueChange(ValueChange.incCovenVar(coven, "ci_members", 1));
        story.valueChange(ValueChange.incCovenVar(coven, "ci_living_space", -1));
        story.setIntProperty("si_registerStudent", 1);
    }

    @Override public Set<String> getRequiredInputs() {
        return REQUIRED_INPUT;
    }

    @Override public Set<String> getOutputs() {
        return OUTPUTS;
    }
}
