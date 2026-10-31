package com.fxmind.exception;

/** A requested change would violate an invariant of the system (e.g. removing the last administrator). */
public final class BusinessRuleException extends FxMindException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
