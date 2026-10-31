package com.fxmind.service;

import com.fxmind.config.FxMindProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Translates between the MetaTrader server ("broker") wall clock and UTC instants.
 *
 * <p>MQL clients send timestamps as {@code yyyy.MM.dd HH:mm} strings in broker time; everything is
 * persisted in UTC.
 */
@Component
public class BrokerClock {

    public static final DateTimeFormatter MT_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private final Clock clock;
    private final ZoneId brokerZone;

    public BrokerClock(Clock clock, FxMindProperties properties) {
        this.clock = clock;
        this.brokerZone = properties.broker().timeZone();
    }

    public Instant now() {
        return clock.instant();
    }

    public Instant parseBrokerTime(String metaTraderTimestamp) {
        return toInstant(LocalDateTime.parse(metaTraderTimestamp.strip(), MT_FORMAT));
    }

    public Instant toInstant(LocalDateTime brokerTime) {
        return brokerTime.atZone(brokerZone).toInstant();
    }

    public LocalDateTime toBrokerTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, brokerZone);
    }

    public String formatBrokerTime(Instant instant) {
        return toBrokerTime(instant).format(MT_FORMAT);
    }

    public ZoneId brokerZone() {
        return brokerZone;
    }
}
