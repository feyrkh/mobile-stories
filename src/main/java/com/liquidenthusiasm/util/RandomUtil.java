package com.liquidenthusiasm.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomUtil {

    private static final Logger log = LoggerFactory.getLogger(RandomUtil.class);

    private static Random r = new Random();

    public static <T> T randomChoice(T[] options) {
        if (options == null || options.length == 0)
            return null;
        return options[r.nextInt(options.length)];
    }
}
