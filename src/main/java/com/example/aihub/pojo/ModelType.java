package com.example.aihub.pojo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ModelType {
    DEEPSEEK("deepseek-r1"),
    DOUBAO("doubao-1.5pro");

    @JsonValue
    private final String value;

    public String getValue() {
        return value;
    }

    public static ModelType fromString(String value) {
        for (ModelType type : ModelType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + value);
    }

    ModelType(String value) {
        this.value = value;
    }
}
