package com.fxmind.exception;

/** An external data source could not be fetched or its payload could not be parsed. */
public final class DataProviderException extends FxMindException {

    private final String provider;

    public DataProviderException(String provider, String message) {
        super("[" + provider + "] " + message);
        this.provider = provider;
    }

    public DataProviderException(String provider, String message, Throwable cause) {
        super("[" + provider + "] " + message, cause);
        this.provider = provider;
    }

    public String provider() {
        return provider;
    }
}
