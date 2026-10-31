package com.fxmind.dto;

import com.fxmind.domain.Importance;
import java.time.Instant;
import java.util.Objects;

/** Provider-neutral calendar event as scraped from a source, before it is resolved and persisted. */
public record ParsedNewsEvent(
        String currency,
        String name,
        Importance importance,
        Instant happenTime,
        String actualValue,
        String forecastValue,
        String previousValue) {

    public ParsedNewsEvent {
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(importance, "importance");
        Objects.requireNonNull(happenTime, "happenTime");
    }
}
