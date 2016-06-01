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

public class WeightedChance implements StoryFunction, FunctionWithInputs, FunctionWithOutputs {

    private static final Logger log = LoggerFactory.getLogger(WeightedChance.class);

    public static final Set<String> OUTPUTS = Sets.newHashSet("ss_chance");

    public static final Set<String> REQUIRED_INPUT = Sets.newHashSet("stat", "base_chance");

    public static final Set<String> OPTIONAL_INPUT = Sets.newHashSet("chance_mod", "stat_base");

    @Override public void call(Map<String, Object> args, Coven coven, Person person, StoryInstance story) {
        Object outputVar = args.get("output");
        if (outputVar == null) {
            outputVar = "ss_chance";
        }
        int adjustment = calculateChanceOutOf1000(args, coven, person, story);

        int retval = -(((int) (Math.random() * 1000)) - adjustment);

        story.state(outputVar.toString(), adjustment);
    }

    public int calculateChanceOutOf1000(Map<String, Object> args, Coven coven, Person person, StoryInstance story) {
        String stat = (String) args.get("stat");  // Variable name to base the chances on
        int baseChance = argToInt(args, "base_chance"); // Default chance (from 0 to 1000) without any modifications
        int chanceMod = argToInt(args, "chance_mod",
            10); // How much to change the base_chance for every point of 'stat' over/under the 'base_stat' level
        int base_stat =
            argToInt(args, "stat_base", 0); // What level we expect the 'stat' to be at in order for 'base_chance' to be the resulting value

        int statValue = Daos.varRepo.getIntProperty(stat, coven, person, story.getState());
        int adjustment = baseChance + (statValue - base_stat) * chanceMod;
        if (adjustment < 0)
            adjustment = 0;
        if (adjustment > 1000)
            adjustment = 1000;
        return adjustment;
    }

    @Override public Set<String> getRequiredInputs() {
        return REQUIRED_INPUT;
    }

    @Override public Set<String> getOutputs() {
        return OUTPUTS;
    }
}
