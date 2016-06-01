package com.liquidenthusiasm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidVariableName extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(InvalidVariableName.class);

    public InvalidVariableName(String message) {
        super(message);
    }
}
