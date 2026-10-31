package com.fxmind.thrift;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.domain.Importance;
import com.fxmind.dto.SentimentAverage;
import com.fxmind.dto.UpcomingNewsEvent;
import com.fxmind.port.MarketDataQueryPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class FxMindMqlHandlerTest {

    private final MarketDataQueryPort port = mock(MarketDataQueryPort.class);
    private final FxMindMqlHandler handler = new FxMindMqlHandler(port);

    @Test
    void nextNewsEventIsEncodedAsFourStrings() {
        var event = new UpcomingNewsEvent("USD", "NFP", Importance.HIGH, LocalDateTime.parse("2026-11-06T15:30"));
        when(port.nextNewsEvent("EURUSD", "2026.11.06 10:00", 1)).thenReturn(Optional.of(event));
        when(port.formatBrokerTime(event)).thenReturn("2026.11.06 15:30");

        var result = handler.ProcessStringData(
                Map.of("func", "NextNewsEvent", "symbol", "EURUSD", "time", "2026.11.06 10:00", "importance", "1"),
                List.of());

        assertThat(result).containsExactly("USD", "2", "2026.11.06 15:30", "NFP");
    }

    @Test
    void currentSentimentsFallsBackToMinusOne() {
        when(port.currentSentiment("EURUSD", "2026.11.06 10:00")).thenReturn(SentimentAverage.EMPTY);

        assertThat(handler.ProcessDoubleData(
                        Map.of("func", "CurrentSentiments", "symbol", "EURUSD", "time", "2026.11.06 10:00"), List.of()))
                .containsExactly(-1.0, -1.0);
    }

    @Test
    void sentimentsArrayDelegatesWithSite() {
        when(port.sentimentHistory("EURUSD", List.of("t1", "t2"), 4)).thenReturn(List.of(55.0, -125.0));

        assertThat(handler.ProcessDoubleData(
                        Map.of("func", "SentimentsArray", "symbol", "EURUSD", "site", "4"), List.of("t1", "t2")))
                .containsExactly(55.0, -125.0);
    }

    @Test
    void badRequestsYieldEmptyResultsInsteadOfExceptions() {
        assertThat(handler.ProcessStringData(Map.of(), List.of())).isEmpty();
        assertThat(handler.ProcessStringData(Map.of("func", "NextNewsEvent"), List.of()))
                .isEmpty();
        assertThat(handler.ProcessDoubleData(Map.of("func", "Nope"), List.of())).isEmpty();
        assertThat(handler.IsServerActive(Map.of())).isEqualTo(1L);
    }
}
