package com.example.aihub.exception;

public class MyIllegalArgumentException extends BussinessException {
    public MyIllegalArgumentException(String message) {
        super(message);
    }

    public MyIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
