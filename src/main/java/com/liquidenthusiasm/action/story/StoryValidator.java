package com.liquidenthusiasm.action.story;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liquidenthusiasm.domain.Coven;
import com.liquidenthusiasm.domain.Person;
import com.liquidenthusiasm.domain.StoryInstance;

public class StoryValidator {

    private static final Logger log = LoggerFactory.getLogger(StoryValidator.class);

    public static String validate(String fieldName, String fieldLabel, String validationRuleName, Object validationValue,
        Map<String, String> formValues,
        Coven coven,
        Person person, StoryInstance story) {
        String thisFormValue = formValues.get(fieldName);
        switch (validationRuleName) {
        case "maxLength":
            return maxLength(fieldLabel, (int) validationValue, thisFormValue);
        case "minLength":
            return minLength(fieldLabel, (int) validationValue, thisFormValue);
        }
        return null;
    }

    private static String maxLength(String fieldName, int validationValue, String thisFormValue) {
        if (thisFormValue.length() > validationValue) {
            return String.format("%s must be no more than %d characters long", fieldName, validationValue);
        }
        return null;
    }

    private static String minLength(String fieldName, int validationValue, String thisFormValue) {
        if (thisFormValue.length() < validationValue) {
            return String.format("%s must be at least %d characters long", fieldName, validationValue);
        }
        return null;
    }

    public static String validateSelectField(FieldDef fieldDef, Map<String, String> formValues, Coven coven, Person person,
        StoryInstance story) {
        String selectedOption = null;
        if (formValues != null) {
            selectedOption = formValues.get(fieldDef.getName());
        }
        if (selectedOption == null) {
            return String.format("No value was selected for %s", fieldDef.getLabel());
        }
        for (FieldDefSelectOption fieldDefSelectOption : fieldDef.getOptions()) {
            if (selectedOption.equals(fieldDefSelectOption.getValue())) {
                return null;
            }
        }
        // Wasn't found in the list
        return String.format("Unexpected value '%s' was selected for field '%s'", selectedOption, fieldDef.getLabel());
    }
}
