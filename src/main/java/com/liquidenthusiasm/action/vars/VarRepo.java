package com.liquidenthusiasm.action.vars;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ValidationException;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;
import com.liquidenthusiasm.domain.ValueChange;
import com.liquidenthusiasm.util.InvalidVariableName;

public class VarRepo {

    private static final Logger log = LoggerFactory.getLogger(VarRepo.class);

    private static Pattern varNamePattern = Pattern.compile(".*?\\{\\{(.+?)}}.*", Pattern.DOTALL);

    public static final int VAR_TYPE = 1;

    public static final int VAR_SCOPE = 0;

    private static final ObjectMapper mapper = new ObjectMapper();

    private Map<String, VarDescription> vars = new TreeMap<>(new Comparator<String>() {

        @Override public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    });

    private Map<String, String> varPostfixMap = new HashMap<>();

    private static final TypeReference<List<VarDescription>> VAR_DESCRIPTION_TYPEREF = new TypeReference<List<VarDescription>>() {

    };

    public void loadVars(String filename) throws IOException {
        log.info("Loading variable description list {}", filename);
        String json = Resources.toString(Resources.getResource(filename), Charsets.UTF_8);
        List<VarDescription> vars = mapper.readValue(json, VAR_DESCRIPTION_TYPEREF);
        // Make sure all the vars are good before adding any of them
        for (VarDescription var : vars) {
            var.setFilename(filename);
            var.validate();
            String variable = var.getName();
            String varPostfix = variable.split("_", 2)[1];
            if (varPostfixMap.containsKey(varPostfix) && !variable.equals(varPostfixMap.get(varPostfix))) {
                throw new ValidationException(
                    "A variable named '" + varPostfixMap.get(varPostfix) + "' already exists, adding '" + variable
                        + "' would be confusing...please rename it.");
            }
            varPostfixMap.put(varPostfix, variable);
            if (this.vars.containsKey(var.name)) {
                throw new ValidationException(String
                    .format("Duplicate variable name=%s in %s - already defined once in %s", var.name, var.getFilename(),
                        this.vars.get(var.name).getFilename()));
            }
        }

        // Add the vars
        for (VarDescription var : vars) {
            addVar(var);
        }

    }

    private void addVar(VarDescription var) {
        vars.put(var.getName(), var);
    }

    public Map<String, VarDescription> getVars() {

        return ImmutableMap.copyOf(vars);
    }

    public VarDescription getVar(String variableName) {
        if (vars.containsKey(variableName))
            return vars.get(variableName);
        else {
            VarDescription emptyDesc = new VarDescription();
            emptyDesc.name = variableName;
            emptyDesc.setUndefined(true);
            return emptyDesc;
        }
    }

    public void validate(String variable) {
        if (StringUtils.isBlank(variable))
            throw new ValidationException("Variable name may not be null or blank");
        if (variable.charAt(2) != '_')
            throw new ValidationException("Expect var name in the format: <scope_char><type_char>_<var_name>, but got '" + variable + "'");
        if (variable.length() < 4)
            throw new ValidationException("Expect var name in the format: <scope_char><type_char>_<var_name>, but got '" + variable + "'");
        char scope = variable.charAt(0);
        char type = variable.charAt(1);
        if (scope != 'c' && scope != 'p' && scope != 's')
            throw new ValidationException("Invalid scope '" + scope + "' in variable name " + variable);
        if (type != 'i' && type != 's')
            throw new ValidationException("Invalid var type '" + type + "' in variable name " + variable);
        if (scope != 's' && !this.vars.containsKey(variable))
            throw new ValidationException("Unregistered variable name: " + variable);
    }

    public String getVarLabel(String var) {
        VarDescription vd = getVar(var);
        if (vd == null)
            return var;
        return vd.getLabel();
    }

    public Object getProperty(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (getPropertyType(variable)) {
        case 's':
            return getStrProperty(variable, coven, person, storyState);
        case 'i':
            return getIntProperty(variable, coven, person, storyState);
        default:
            throw new InvalidVariableName("Unexpected variable type in " + variable + ": " + getPropertyType(variable));
        }
    }

    public void setProperty(String variable, Object val, Coven coven, Person person, StoryInstance story) {
        switch (getPropertyType(variable)) {
        case 's':
            setStrProperty(variable, String.valueOf(val), coven, person, story);
            break;
        case 'i':
            setIntProperty(variable, (int) val, coven, person, story);
            break;
        default:
            throw new InvalidVariableName("Unexpected variable type in " + variable + ": " + getPropertyType(variable));
        }
    }

    private void setStrProperty(String variable, String val, Coven coven, Person person, StoryInstance story) {
        switch (getPropertyScope(variable)) {
        case 'c':
            if (coven != null)
                coven.setStrProperty(variable, val);
            break;
        case 'p':
            if (person != null)
                person.setStrProperty(variable, val);
            break;
        case 's':
            if (story != null)
                story.state(variable, val);
            break;
        default:
            throw new InvalidVariableName("Unexpected variable scope in " + variable + ": " + getPropertyScope(variable));
        }
    }

    private void setIntProperty(String variable, int val, Coven coven, Person person, StoryInstance story) {
        switch (getPropertyScope(variable)) {
        case 'c':
            if (coven != null)
                coven.setIntProperty(variable, val);
            break;
        case 'p':
            if (person != null)
                person.setIntProperty(variable, val);
            break;
        case 's':
            if (story != null)
                story.state(variable, val);
            break;
        default:
            throw new InvalidVariableName("Unexpected variable scope in " + variable + ": " + getPropertyScope(variable));
        }
    }

    public static char getPropertyType(String variable) {
        return variable.charAt(VAR_TYPE);
    }

    public int getIntProperty(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        Integer val = getIntPropertyIfExists(variable, coven, person, storyState);
        if (val == null)
            return 0;
        return val.intValue();
    }

    public static char getPropertyScope(String variable) {
        return variable.charAt(VAR_SCOPE);
    }

    public String getStrProperty(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (getPropertyScope(variable)) {
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
            Object val = storyState.get(variable);
            if (val == null)
                return null;
            return String.valueOf(val);
        default:
            throw new InvalidVariableName("Unexpected variable scope in " + variable + ": " + getPropertyScope(variable));
        }
    }

    public String getPropertyDescription(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        VarDescription varDesc = Daos.varRepo.getVar(variable);
        if (!varDesc.hasValueDescriptions()) {
            return varDesc.getDesc();
        }
        String varValue = String.valueOf(getProperty(variable, coven, person, storyState));
        String retval = varDesc.getValueDesc(varValue);
        if (retval == null) {
            log.error("Missing variable description. varName={}, varValue=\"{}\"", variable, varValue);
            return varDesc.getDesc();
        }
        return retval;
    }

    public String interpolate(String oldStr, Coven coven, Person person, StoryInstance story) {
        return interpolate(oldStr, coven, person, story.getState());
    }

    public String interpolate(String oldStr, Coven coven, Person person, Map<String, Object> state) {
        if (oldStr == null) {
            return null;
        }
        String retval = oldStr;
        Matcher matcher = varNamePattern.matcher(retval);
        while (matcher.matches()) {
            String varName = matcher.group(1);
            String varValue;
            if (varName.startsWith("#")) {
                varValue = getPropertyDescription(varName.substring(1), coven, person, state);
            } else {
                varValue = String.valueOf(getProperty(varName, coven, person, state));
            }
            if (varValue.contains("{{"))
                varValue = "__BAD_REPLACEMENT_STRING__:varName";
            retval = retval.replaceAll("\\{\\{" + varName + "\\}\\}", varValue);
            matcher = varNamePattern.matcher(retval);
        }
        return retval;
    }

    private static Pattern STAT_CHANGE_PATTERN = Pattern.compile("(.+?)\\s*(\\+|-|=)(.+)");

    public void processStatAdd(String add, Coven coven, Person person, StoryInstance story) {
        Matcher m = getStatAddMatcher(add, coven, person, story);
        if (m == null)
            return;
        String varName = m.group(1);
        String op = m.group(2);
        String amount = m.group(3);
        if ("=".equals(op)) {
            performValueSet(varName, amount, coven, person, story);
        } else {
            performValueAdd(varName, Integer.parseInt(op + amount), coven, person, story);
        }
    }

    private Matcher getStatAddMatcher(String add, Coven coven, Person person, StoryInstance story) {
        String interpolatedAdd = interpolate(add, coven, person, story);
        Matcher m = STAT_CHANGE_PATTERN.matcher(interpolatedAdd);
        if (!m.matches()) {
            log.error("Invalid stat change: '" + add + "' (interpolated as '" + interpolatedAdd + "')");
            return null;
        }
        return m;
    }

    public void performValueAdd(String var, int amt, Coven coven, Person person, StoryInstance story) {
        switch (Daos.varRepo.getPropertyScope(var)) {
        case 'c':
            story.valueChange(ValueChange.incCovenVar(coven, var, amt));
            break;
        case 'p':
            story.valueChange(ValueChange.incPersonVar(person, var, amt));
            break;
        case 's':
            story.valueChange(ValueChange.incStoryVar(story, var, amt));
            break;
        }
    }

    public void performValueSet(String var, Object val, Coven coven, Person person, StoryInstance story) {
        if (Daos.varRepo.getPropertyType(var) == 'i') {
            if (val == null) {
                val = "0";
            }
            int amt = Integer.parseInt(String.valueOf(val));
            switch (Daos.varRepo.getPropertyScope(var)) {
            case 'c':
                story.valueChange(ValueChange.setCovenVar(coven, var, amt));
                break;
            case 'p':
                story.valueChange(ValueChange.setPersonVar(person, var, amt));
                break;
            case 's':
                story.valueChange(ValueChange.setStoryVar(story, var, amt));
                break;
            }
        } else {
            String newVal = String.valueOf(val);
            switch (Daos.varRepo.getPropertyScope(var)) {
            case 'c':
                story.valueChange(ValueChange.setCovenVar(coven, var, newVal));
                break;
            case 'p':
                story.valueChange(ValueChange.setPersonVar(person, var, newVal));
                break;
            case 's':
                story.valueChange(ValueChange.setStoryVar(story, var, newVal));
                break;
            }
        }
    }

    public Object getPropertyIfExists(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (getPropertyType(variable)) {
        case 's':
            return getStrProperty(variable, coven, person, storyState);
        case 'i':
            return getIntPropertyIfExists(variable, coven, person, storyState);
        default:
            throw new InvalidVariableName("Unexpected variable type in " + variable + ": " + getPropertyType(variable));
        }
    }

    public Integer getIntPropertyIfExists(String variable, Coven coven, Person person, Map<String, Object> storyState) {
        switch (getPropertyScope(variable)) {
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
            return stateVar == null ? null : Integer.parseInt(String.valueOf(stateVar));
        default:
            throw new InvalidVariableName("Unexpected variable scope in " + variable + ": " + getPropertyScope(variable));
        }
    }
}
