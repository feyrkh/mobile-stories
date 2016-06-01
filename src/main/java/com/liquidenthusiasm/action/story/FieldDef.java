package com.liquidenthusiasm.action.story;

import java.util.Map;

import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldDef {

    private static final Logger log = LoggerFactory.getLogger(FieldDef.class);

    public enum FieldType {text, select}

    private String label;

    private String name;

    private FieldType type;

    private String defaultValue;

    private FieldDefSelectOption[] options;

    private Map<String, Object> validation;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    private Integer width;

    public void validate() {
        try {
            if (options != null) {
                for (FieldDefSelectOption option : options) {
                    option.validate();
                }
            }
        } catch (Exception e) {
            throw new ValidationException("Error while validating " + this, e);
        }
    }

    @Override public String toString() {
        return "FieldDef{" +
            "name='" + name + '\'' +
            '}';
    }

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

    public FieldDefSelectOption[] getOptions() {
        return options;
    }

    public void setOptions(FieldDefSelectOption[] options) {
        this.options = options;
    }

    public Map<String, Object> getValidation() {
        return validation;
    }

    public void setValidation(Map<String, Object> validation) {
        this.validation = validation;
    }
}
