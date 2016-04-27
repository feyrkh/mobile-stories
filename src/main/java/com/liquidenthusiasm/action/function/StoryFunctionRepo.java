package com.liquidenthusiasm.action.function;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.action.story.StoryCallInputMapping;
import com.liquidenthusiasm.action.story.StoryFunction;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.util.VariableLookup;

public class StoryFunctionRepo {

    private static final Logger log = LoggerFactory.getLogger(StoryFunctionRepo.class);

    private final Map<String, StoryFunction> functions = new HashMap<>();

    public void call(String functionName, StoryCallInputMapping[] inputs, Coven coven, Person person, StoryInstance story) {
        StoryFunction f = functions.get(functionName);
        if (f == null) {
            log.error("Missing StoryFunction in repo functionName={}", functionName);
            return;
        }
        Map<String, Object> args = new HashMap<>();
        if (inputs != null) {
            for (StoryCallInputMapping input : inputs) {
                args.put(input.getFunctionVarName(),
                    VariableLookup.interpolate(input.getContextVarName(), coven, person, story));
            }
        }

        f.call(args, coven, person, story);
    }

    public void put(String functionName, StoryFunction function) {
        functions.put(functionName, function);
    }
}
