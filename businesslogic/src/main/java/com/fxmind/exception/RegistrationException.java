package com.fxmind.exception;

/** A sign-up or activation request violates a business rule (e.g. e-mail already registered). */
public final class RegistrationException extends FxMindException {

    public RegistrationException(String message) {
        super(message);
    }
}
