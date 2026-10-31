package com.fxmind.provider.news;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fxmind.Fixtures;
import com.fxmind.domain.Importance;
import com.fxmind.dto.ParsedNewsEvent;
import com.fxmind.exception.DataProviderException;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class ForexFactoryNewsProviderTest {

    private final ForexFactoryNewsProvider provider =
            new ForexFactoryNewsProvider((name, uri) -> Fixtures.load("forexfactory-week.json"), new JsonMapper());

    @Test
    void parsesFeedNormalisingTimesToUtcAndSkippingHolidays() {
        var events = provider.fetchEvents();

        assertThat(events)
                .extracting(ParsedNewsEvent::currency, ParsedNewsEvent::importance, ParsedNewsEvent::happenTime)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(
                                "USD", Importance.HIGH, Instant.parse("2026-11-06T13:30:00Z")),
                        org.assertj.core.groups.Tuple.tuple(
                                "EUR", Importance.MEDIUM, Instant.parse("2026-10-30T13:00:00Z")),
                        org.assertj.core.groups.Tuple.tuple(
                                "USD", Importance.LOW, Instant.parse("2026-11-02T08:00:00Z")));
        assertThat(events.getFirst().forecastValue()).isEqualTo("120K");
        assertThat(events.getLast().forecastValue()).isNull();
    }

    @Test
    void rejectsMalformedPayload() {
        assertThatThrownBy(() -> provider.parse("<html>maintenance</html>"))
                .isInstanceOf(DataProviderException.class)
                .hasMessageContaining("forexfactory");
    }
}
