package com.liquidenthusiasm;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface CleanView {

    @JsonIgnore Map<String, Object> getCleanView();
}
