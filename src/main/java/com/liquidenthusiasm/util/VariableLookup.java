package com.liquidenthusiasm.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

public class VariableLookup {

    private static final Logger log = LoggerFactory.getLogger(VariableLookup.class);

    private static Pattern varNamePattern = Pattern.compile(".*?\\{\\{(.+?)}}.*");

    public static final int VAR_TYPE = 1;

    public static final int VAR_SCOPE = 0;

    public static Object getProperty(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (variable.charAt(VAR_TYPE)) {
        case 's':
            return getStrProperty(variable, coven, person, storyState);
        case 'i':
            return getIntProperty(variable, coven, person, storyState);
        default:
            throw new RuntimeException("Unexpected variable type in " + variable + ": " + variable.charAt(VAR_TYPE));
        }
    }

    public static int getIntProperty(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (variable.charAt(VAR_SCOPE)) {
        case 'c':
            if (coven == null)
                return 0;
            return coven.getIntProperty(variable);
        case 'p':
            if (person == null)
                return 0;
            return person.getIntProperty(variable);
        case 's':
            if (storyState == null)
                return 0;
            Object stateVar = storyState.get(variable);
            return (int) (stateVar == null ? 0 : stateVar);
        default:
            throw new RuntimeException("Unexpected variable scope in " + variable + ": " + variable.charAt(VAR_SCOPE));
        }
    }

    public static String getStrProperty(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (variable.charAt(VAR_SCOPE)) {
        case 'c':
            if (coven == null)
                return null;
            return coven.getStrProperty(variable);
        case 'p':
            if (person == null)
                return null;
            return person.getStrProperty(variable);
        case 's':
            if (storyState == null)
                return null;
            return String.valueOf(storyState.get(variable));
        default:
            throw new RuntimeException("Unexpected variable scope in " + variable + ": " + variable.charAt(VAR_SCOPE));
        }
    }

    public static String interpolate(String oldStr, Coven coven, Person person, StoryInstance story) {
        String retval = oldStr;
        Matcher matcher = varNamePattern.matcher(retval);
        while (matcher.matches()) {
            String varName = matcher.group(1);
            String varValue = String.valueOf(VariableLookup.getProperty(varName, coven, person, story.getState()));
            if (varValue.contains("{{"))
                varValue = "__BAD_REPLACEMENT_STRING__:varName";
            retval = retval.replaceAll("\\{\\{" + varName + "\\}\\}", varValue);
            matcher = varNamePattern.matcher(retval);
        }
        return retval;
    }
}
