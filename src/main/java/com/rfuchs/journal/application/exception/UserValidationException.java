package com.rfuchs.journal.application.exception;

/**
 * Created by rfuchs on 17/04/2016.
 */
public class UserValidationException extends RuntimeException {
    public UserValidationException(final String message) {
        super(message);
    }
}
