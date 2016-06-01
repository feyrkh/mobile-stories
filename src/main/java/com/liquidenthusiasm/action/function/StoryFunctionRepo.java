package com.liquidenthusiasm.action.function;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.liquidenthusiasm.action.story.StoryCallInputMapping;
import com.liquidenthusiasm.action.story.StoryFunction;
import com.liquidenthusiasm.dao.Daos;
import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

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
                    Daos.varRepo.interpolate(input.getContextVarName(), coven, person, story));
            }
        }

        f.call(args, coven, person, story);
    }

    public void put(String functionName, StoryFunction function) {
        functions.put(functionName, function);
    }

    public void validate(StoryFunction function, StoryCallInputMapping[] inputs) {
        if (function instanceof FunctionWithInputs) {
            Set<String> functionInputNames = Arrays.stream(inputs)
                .map(StoryCallInputMapping::getFunctionVarName)
                .collect(Collectors.toSet());

            FunctionWithInputs functionWithInputs = (FunctionWithInputs) function;
            Sets.SetView<String> missingRequired = Sets.difference(functionWithInputs.getRequiredInputs(), functionInputNames);
            Sets.SetView<String> nonrequiredProvided = Sets.difference(functionInputNames, functionWithInputs.getRequiredInputs());
            Sets.SetView<String> unexpected = Sets.difference(nonrequiredProvided, functionWithInputs.getOptionalInputs());
            StringBuilder errors = new StringBuilder();
            if (missingRequired.size() > 0) {
                errors.append("Required arguments were not provided. missingRequired=").append(missingRequired).append("\n");
            }
            if (unexpected.size() > 0) {
                errors.append("Unexpected arguments (not required or optional) were provided. unexpectedArgs=").append(unexpected);
            }
            if (errors.length() > 0) {
                throw new ValidationException(errors.toString());
            }
        }
    }

    public void validate(String functionName, StoryCallInputMapping[] inputs) {
        if (!functions.containsKey(functionName)) {
            throw new ValidationException("Missing function name: " + functionName);
        }
        try {
            validate(functions.get(functionName), inputs);
        } catch (Exception e) {
            throw new ValidationException("Error validating function " + functionName, e);
        }
    }

    public Map<String, StoryFunction> getFunctions() {
        return functions;
    }
}
