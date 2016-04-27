package com.liquidenthusiasm.action.story;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = StoryCallInputMapping.Serializer.class)
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

    public static class Serializer extends JsonSerializer<StoryCallInputMapping> {

        @Override
        public void serialize(StoryCallInputMapping storyCallInputMapping, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
            jsonGenerator.writeString(String.format("%s->%s", storyCallInputMapping.contextVarName, storyCallInputMapping.functionVarName));
        }
    }
}
