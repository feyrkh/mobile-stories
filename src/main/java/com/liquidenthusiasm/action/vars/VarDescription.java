package com.liquidenthusiasm.action.vars;

import java.nio.charset.Charset;
import java.util.Map;

import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public class VarDescription {

    private static final Logger log = LoggerFactory.getLogger(VarDescription.class);

    public static final String ASSETS_ICON_DEFAULT_PNG = "assets/icon/default.png";

    String name;

    String label;

    String desc;

    boolean hideStoryOption = false;

    boolean hideChanges = false;

    String image;

    boolean undefined;

    private String filename;

    private int min = 0;

    private int max = 0;

    private Map<String, String> valueDescriptions = null;

    public VarDescription() {
    }

    public boolean isUndefined() {
        return undefined;
    }

    public void setUndefined(boolean undefined) {
        this.undefined = undefined;
    }

    public void validate() {
        if (StringUtils.isEmpty(filename))
            validationException("filename");
        if (StringUtils.isEmpty(name))
            validationException("name");
        if ((!hideChanges || !hideStoryOption) && StringUtils.isEmpty(label))
            validationException("label");
        if ((!hideChanges || !hideStoryOption) && StringUtils.isEmpty(desc))
            validationException("desc");
        if (StringUtils.isEmpty(filename))
            filename = ASSETS_ICON_DEFAULT_PNG;
        try {
            Resources.toString(Resources.getResource(filename), Charset.forName("UTF-8"));
        } catch (Exception e) {
            throw new ValidationException("Icon file '" + filename + " does not exist for variable: %s");
        }
    }

    private void validationException(String fieldName) throws ValidationException {
        throw new ValidationException(String.format("Expected '%s' field for variable: %s", fieldName, this.toString()));
    }

    @Override public String toString() {
        return "VarDescription{" +
            "filename='" + filename + '\'' +
            ", name='" + name + '\'' +
            '}';
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        if (label == null)
            return name;
        return label;
    }

    public String getDesc() {
        return desc;
    }

    public String getValueDesc(String value) {
        if (valueDescriptions == null)
            return value;
        if (valueDescriptions.containsKey(value)) {
            if (this.name.charAt(1) == 'i') {
                return String.format("[%s] %s", value, valueDescriptions.get(value));
            }
            return valueDescriptions.get(value);
        } else {
            if (value != "none")
                log.error("Missing valueDescription for var={}, value={}", name, value);
            return value;
        }
    }

    public boolean gethideStoryOption() {
        return hideStoryOption;
    }

    public boolean getHideChanges() {
        return hideChanges;
    }

    public String getImage() {
        if (image == null) {
            return ASSETS_ICON_DEFAULT_PNG;
        }
        return image;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public boolean hasValueDescriptions() {
        return valueDescriptions != null;
    }

    public Map<String, String> getValueDescriptions() {
        return valueDescriptions;
    }

    public void setValueDescriptions(Map<String, String> valueDescriptions) {
        this.valueDescriptions = valueDescriptions;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
