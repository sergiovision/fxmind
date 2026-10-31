package com.fxmind.provider;

/**
 * Strategy for one external market-data source. Implementations are discovered as Spring beans and
 * indexed by {@link #key()} in the {@link ProviderRegistry}; a scheduled job references a provider
 * only by that key.
 */
public sealed interface DataProvider permits NewsProvider, PositionRatioProvider {

    /** Stable identifier referenced from job definitions, e.g. {@code "forexfactory"}. */
    String key();

    /** Human-readable name for logs and the admin console. */
    String displayName();
}
