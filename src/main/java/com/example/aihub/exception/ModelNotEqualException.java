package com.example.aihub.exception;

public class ModelNotEqualException extends BussinessException {
    public ModelNotEqualException(String message) {
        super(message);
    }

    public ModelNotEqualException(String message, Throwable cause) {
        super(message, cause);
    }
}
