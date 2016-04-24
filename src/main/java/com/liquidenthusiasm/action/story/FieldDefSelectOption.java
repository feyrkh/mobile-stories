package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldDefSelectOption {

    private static final Logger log = LoggerFactory.getLogger(FieldDefSelectOption.class);

    private String label;

    private String value;

    public static FieldDefSelectOption from(String mappingStr) {
        String[] bits = mappingStr.split("->");
        if (bits.length != 2) {
            throw new RuntimeException("Invalid StoryCallInputMapping string: " + mappingStr);
        }
        FieldDefSelectOption mapping = new FieldDefSelectOption();
        mapping.setLabel(bits[0].trim());
        mapping.setValue(bits[1].trim());
        return mapping;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
