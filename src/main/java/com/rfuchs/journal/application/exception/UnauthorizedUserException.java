package com.rfuchs.journal.application.exception;

/**
 * Created by rfuchs on 17/04/2016.
 */
public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(final String message) {
        super(message);
    }
}
