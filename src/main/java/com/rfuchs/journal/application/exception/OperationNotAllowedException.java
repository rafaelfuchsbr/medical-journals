package com.rfuchs.journal.application.exception;

/**
 * Created by rfuchs on 17/04/2016.
 */
public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(final String message) {
        super(message);
    }
}
