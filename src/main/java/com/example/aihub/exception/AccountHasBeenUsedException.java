package com.example.aihub.exception;

public class AccountHasBeenUsedException extends BussinessException {
    public AccountHasBeenUsedException(String message) {
        super(message);
    }

    public AccountHasBeenUsedException(String message, Throwable cause) {
        super(message, cause);
    }
}
