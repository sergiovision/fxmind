package com.fxmind.dto;

import com.fxmind.domain.Importance;
import java.time.Instant;

/** Read model of a calendar event for grids and APIs. */
public record NewsEventDto(
        Long id,
        String currency,
        String name,
        Importance importance,
        Instant happenTime,
        String actualValue,
        String forecastValue,
        String previousValue,
        Instant parsedAt,
        boolean raised) {}
