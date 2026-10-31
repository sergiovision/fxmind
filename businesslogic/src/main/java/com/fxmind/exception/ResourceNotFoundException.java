package com.fxmind.exception;

/** A requested aggregate does not exist. */
public final class ResourceNotFoundException extends FxMindException {

    public ResourceNotFoundException(String type, Object key) {
        super(type + " not found: " + key);
    }
}
