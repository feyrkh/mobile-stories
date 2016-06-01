package com.liquidenthusiasm.action.function;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.liquidenthusiasm.action.story.StoryFunction;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.util.RandomUtil;

public class RandomPersonNameFunc implements StoryFunction, FunctionWithOutputs {

    private static final Logger log = LoggerFactory.getLogger(RandomPersonNameFunc.class);

    public static final Set<String> OUTPUTS = Sets
        .newHashSet("ss_randomPersonName");

    private static String[] phonemes = {
        "abi", "ana", "ane", "erl", "eal", "em", "ph", "tha", "ale", "bin", "sin", "fin",
        "iri", "infa", "ina", "ila", "omo", "oto", "oro", "oni", "oin", "emph",
        "thri", "sri", "graf", "gan", "shu", "nem", "x", "z", "kn", "a", "e", "i", "o", "u"
    };

    Random r = new Random();

    @Override public void call(Map<String, Object> args, Coven coven, Person person, StoryInstance story) {
        StringBuilder sb = new StringBuilder(StringUtils.capitalize(RandomUtil.randomChoice(phonemes)));
        for (int i = 0; i <= r.nextInt(1); i++) {
            sb.append(RandomUtil.randomChoice(phonemes));
        }
        sb.append(' ').append(StringUtils.capitalize(RandomUtil.randomChoice(phonemes)));
        for (int i = 0; i <= r.nextInt(2); i++) {
            sb.append(RandomUtil.randomChoice(phonemes));
        }

        story.state("ss_randomPersonName", sb.toString());
    }

    @Override public Set<String> getOutputs() {
        return OUTPUTS;
    }
}
