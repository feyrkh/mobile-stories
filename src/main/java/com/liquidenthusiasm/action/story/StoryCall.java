package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.function.StoryFunctionRepo;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

public class StoryCall {

    private static final Logger log = LoggerFactory.getLogger(StoryCall.class);

    private String function;

    private StoryCallInputMapping[] inputs;

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public StoryCallInputMapping[] getInputs() {
        return inputs;
    }

    public void setInputs(String[] inputs) {
        if (inputs == null) {
            return;
        }
        this.inputs = new StoryCallInputMapping[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            this.inputs[i] = StoryCallInputMapping.from(inputs[i]);
        }
    }

    public void call(StoryFunctionRepo repo, Coven coven, Person person, StoryInstance story) {
        repo.call(function, inputs, coven, person, story);
    }

    public void validate() {
        Daos.functionRepo.validate(function, inputs);
    }
}
