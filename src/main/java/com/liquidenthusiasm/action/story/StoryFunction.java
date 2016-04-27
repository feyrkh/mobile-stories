package com.liquidenthusiasm.action.story;

import java.util.Map;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

@FunctionalInterface
public interface StoryFunction {

    void call(Map<String, Object> args, Coven coven, Person person, StoryInstance story);
}