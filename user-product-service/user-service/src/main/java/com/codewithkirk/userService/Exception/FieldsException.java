package com.codewithkirk.userService.Exception;

public class FieldsException extends RuntimeException{

    public FieldsException(String message) {
        super(message);
    }

    public FieldsException(String message, Throwable cause) {
        super(message, cause);
    }
}
