package com.fxmind.provider.news;

import static org.assertj.core.api.Assertions.assertThat;

import com.fxmind.Fixtures;
import com.fxmind.domain.Importance;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ExnessNewsProviderTest {

    private final ExnessNewsProvider provider = new ExnessNewsProvider(
            (name, uri) -> Fixtures.load("exness-calendar.html"),
            Clock.fixed(Instant.parse("2026-11-01T00:00:00Z"), ZoneOffset.UTC));

    @Test
    void carriesDateForwardAndSkipsRowsWithoutTime() {
        var events = provider.fetchEvents();

        assertThat(events).hasSize(2);
        assertThat(events.get(0).currency()).isEqualTo("GBP");
        assertThat(events.get(0).happenTime()).isEqualTo(Instant.parse("2026-11-03T09:30:00Z"));
        assertThat(events.get(0).actualValue()).isEqualTo("49.9");
        assertThat(events.get(1).currency()).isEqualTo("USD");
        assertThat(events.get(1).importance()).isEqualTo(Importance.HIGH);
        assertThat(events.get(1).happenTime()).isEqualTo(Instant.parse("2026-11-03T15:00:00Z"));
        assertThat(events.get(1).actualValue()).isNull();
    }

    @Test
    void resolvesYearAcrossNewYear() {
        assertThat(ExnessNewsProvider.nearestDate(java.time.MonthDay.of(1, 3), java.time.LocalDate.of(2026, 12, 29)))
                .isEqualTo(java.time.LocalDate.of(2027, 1, 3));
        assertThat(ExnessNewsProvider.nearestDate(java.time.MonthDay.of(12, 30), java.time.LocalDate.of(2027, 1, 2)))
                .isEqualTo(java.time.LocalDate.of(2026, 12, 30));
        assertThat(ExnessNewsProvider.nearestDate(java.time.MonthDay.of(11, 3), java.time.LocalDate.of(2026, 11, 1)))
                .isEqualTo(java.time.LocalDate.of(2026, 11, 3));
    }
}
