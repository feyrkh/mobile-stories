package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        this.inputs = new StoryCallInputMapping[inputs.length];
        for(int i=0;i<inputs.length;i++) {
            this.inputs[i] = StoryCallInputMapping.from(inputs[i]);
        }
    }
    
}
