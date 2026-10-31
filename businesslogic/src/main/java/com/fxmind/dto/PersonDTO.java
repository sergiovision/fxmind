package com.fxmind.dto;

import com.fxmind.domain.Privilege;
import java.math.BigDecimal;
import java.time.Instant;

/** Read model of a {@code Person} for the admin console; never exposes credentials. */
public record PersonDto(
        Long id,
        String email,
        Privilege privilege,
        BigDecimal balance,
        boolean activated,
        Instant registeredAt,
        String registrationIp,
        Instant subscriptionExpiresAt) {}
