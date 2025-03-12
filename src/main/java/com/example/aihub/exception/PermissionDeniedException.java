package com.example.aihub.exception;

public class PermissionDeniedException extends BussinessException {
    public PermissionDeniedException(String message) {
        super(message);
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
