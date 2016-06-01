package com.liquidenthusiasm.action.story;

import java.util.Map;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

@FunctionalInterface
public interface StoryFunction {

    void call(Map<String, Object> args, Coven coven, Person person, StoryInstance story);

    default int argToInt(Map<String, Object> args, String argName) throws NumberFormatException {
        String valStr = String.valueOf(args.get(argName));
        return Integer.parseInt(valStr);
    }

    default int argToInt(Map<String, Object> args, String argName, int defaultIfInvalid) {
        try {
            return argToInt(args, argName);
        } catch (NumberFormatException e) {
            return defaultIfInvalid;
        }
    }
}