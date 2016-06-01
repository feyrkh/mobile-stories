package com.liquidenthusiasm.action.function;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.liquidenthusiasm.action.story.StoryFunction;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

public class AddFunc implements StoryFunction, FunctionWithInputs {

    private static final Logger log = LoggerFactory.getLogger(AddFunc.class);

    public static final Set<String> REQUIRED_INPUT = Sets.newHashSet("var", "val");

    @Override public void call(Map<String, Object> args, Coven coven, Person person, StoryInstance story) {
        String var = (String) args.get("var");
        Object amtStr = args.get("val");
        if (amtStr == null) {
            amtStr = "0";
        }
        int amt = Integer.parseInt(String.valueOf(amtStr));
        Daos.varRepo.performValueAdd(var, amt, coven, person, story);
    }

    @Override public Set<String> getRequiredInputs() {
        return REQUIRED_INPUT;
    }
}
