package com.fxmind.provider;

import java.net.URI;

/** Retrieves the raw body of a remote resource. Abstracted so providers can be tested offline. */
@FunctionalInterface
public interface PageFetcher {

    /**
     * @throws com.fxmind.exception.DataProviderException on network failure or a non-2xx response
     */
    String fetch(String provider, URI uri);
}
