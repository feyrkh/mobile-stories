package com.liquidenthusiasm.action.function;

import java.util.Collections;
import java.util.Set;

public interface FunctionWithInputs {

    public default Set<String> getRequiredInputs() {
        return Collections.EMPTY_SET;
    }

    public default Set<String> getOptionalInputs() {
        return Collections.EMPTY_SET;
    }
}
