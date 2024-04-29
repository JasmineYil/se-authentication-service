package org.example.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException(String errorMessage) {
        super(errorMessage);
    }
}