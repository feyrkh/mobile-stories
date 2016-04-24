package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoryCallInputMapping {

    private static final Logger log = LoggerFactory.getLogger(StoryCallInputMapping.class);

    private String contextVarName;

    private String functionVarName;

    public static StoryCallInputMapping from(String mappingStr) {
        String[] bits = mappingStr.split("->");
        if (bits.length != 2) {
            throw new RuntimeException("Invalid StoryCallInputMapping string: " + mappingStr);
        }
        StoryCallInputMapping mapping = new StoryCallInputMapping();
        mapping.setContextVarName(bits[0].trim());
        mapping.setFunctionVarName(bits[1].trim());
        return mapping;
    }

    public String getContextVarName() {
        return contextVarName;
    }

    public void setContextVarName(String contextVarName) {
        this.contextVarName = contextVarName;
    }

    public String getFunctionVarName() {
        return functionVarName;
    }

    public void setFunctionVarName(String functionVarName) {
        this.functionVarName = functionVarName;
    }
}
