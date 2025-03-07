package com.example.aihub.exception;

public class InvalidCredentialsException extends BussinessException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}