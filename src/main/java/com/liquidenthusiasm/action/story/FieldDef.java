package com.liquidenthusiasm.action.story;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldDef {

    private static final Logger log = LoggerFactory.getLogger(FieldDef.class);

    public FieldDefSelectOption[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = new FieldDefSelectOption[options.length];
        for (int i = 0; i < options.length; i++) {
            this.options[i] = FieldDefSelectOption.from(options[i]);
        }
    }

    public enum FieldType {text, select}

    private String label;

    private String name;

    private FieldType type;

    private String defaultValue;

    private int minLength;

    private int maxLength;

    private FieldDefSelectOption[] options;

    public static FieldDef text(String label, String name) {
        FieldDef fd = new FieldDef();
        fd.setLabel(label);
        fd.setName(name);
        fd.setType(FieldType.text);
        return fd;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setType(FieldType type) {
        this.type = type;
    }

    public FieldType getType() {
        return type;
    }

    public FieldDef maxLength(int len) {
        maxLength = len;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
