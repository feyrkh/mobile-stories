package com.liquidenthusiasm.action.story;

import java.io.IOException;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = FieldDefSelectOption.Serializer.class)
public class FieldDefSelectOption {

    private static final Logger log = LoggerFactory.getLogger(FieldDefSelectOption.class);

    private String label;

    private String value;

    @JsonCreator
    public static FieldDefSelectOption from(String mappingStr) {
        String[] bits = mappingStr.split("->", 2);
        if (bits.length != 2) {
            bits = new String[] { mappingStr, mappingStr };
        }
        FieldDefSelectOption mapping = new FieldDefSelectOption();
        mapping.setLabel(bits[0].trim());
        mapping.setValue(bits[1].trim());
        return mapping;
    }

    public String getLabel() {
        if (label == null)
            return value;
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        if (value == null)
            return label;
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void validate() {
        if (StringUtils.isEmpty(label) && StringUtils.isEmpty(value))
            throw new ValidationException("Value and label must not both be blank: " + this);
    }

    public static class Serializer extends JsonSerializer<FieldDefSelectOption> {

        @Override
        public void serialize(FieldDefSelectOption so, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
            jsonGenerator.writeString(
                String.format("%s->%s", so.getLabel(), so.getValue()));
        }
    }
}
