package com.example.aihub.pojo;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRespType {
    METADATA("metadata"),
    MESSAGE("message"),
    END("end");

    @JsonValue
    private final String value;

    ChatRespType(String value) {
        this.value = value;
    }
}
