package com.fxmind.dto;

import com.fxmind.domain.Importance;
import java.time.LocalDateTime;

/** Next relevant calendar event for a symbol, expressed in broker (MetaTrader server) time. */
public record UpcomingNewsEvent(String currency, String name, Importance importance, LocalDateTime brokerTime) {}
