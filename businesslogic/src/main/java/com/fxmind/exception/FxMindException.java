package com.fxmind.exception;

/**
 * Root of the application's exception hierarchy. Sealed so that callers can handle every failure
 * category exhaustively with a pattern-matching {@code switch}.
 */
public abstract sealed class FxMindException extends RuntimeException
        permits DataProviderException, ResourceNotFoundException, RegistrationException, BusinessRuleException {

    protected FxMindException(String message) {
        super(message);
    }

    protected FxMindException(String message, Throwable cause) {
        super(message, cause);
    }
}
