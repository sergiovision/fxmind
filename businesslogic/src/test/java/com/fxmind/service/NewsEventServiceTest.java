package com.fxmind.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fxmind.config.FxMindProperties;
import com.fxmind.domain.Currency;
import com.fxmind.domain.Importance;
import com.fxmind.domain.NewsEvent;
import com.fxmind.dto.ParsedNewsEvent;
import com.fxmind.mapper.DomainMapper;
import com.fxmind.repository.CurrencyRepository;
import com.fxmind.repository.NewsEventRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

class NewsEventServiceTest {

    static final Instant NOW = Instant.parse("2026-10-31T10:00:00Z");

    private final NewsEventRepository events = mock(NewsEventRepository.class);
    private final CurrencyRepository currencies = mock(CurrencyRepository.class);
    private final NewsEventService service =
            new NewsEventService(events, currencies, brokerClock(), mock(DomainMapper.class));

    static BrokerClock brokerClock() {
        return new BrokerClock(Clock.fixed(NOW, ZoneOffset.UTC), properties());
    }

    static FxMindProperties properties() {
        return new Binder(new MapConfigurationPropertySource()).bindOrCreate("fxmind", FxMindProperties.class);
    }

    @Test
    void storesNewEventsAndSkipsDuplicatesAndUnknownCurrencies() {
        var usd = new Currency("USD");
        when(currencies.findByNameIgnoreCase("USD")).thenReturn(Optional.of(usd));
        when(currencies.findByNameIgnoreCase("XXX")).thenReturn(Optional.empty());
        var duplicateTime = NOW.plusSeconds(3600);
        when(events.existsByCurrencyAndHappenTimeAndImportance(usd, duplicateTime, Importance.HIGH))
                .thenReturn(true);

        var result = service.ingest(
                "test",
                List.of(
                        event("USD", NOW.plusSeconds(60), Importance.MEDIUM),
                        event("USD", duplicateTime, Importance.HIGH),
                        event("XXX", NOW, Importance.LOW)));

        assertThat(result.fetched()).isEqualTo(3);
        assertThat(result.stored()).isEqualTo(1);
        assertThat(result.skipped()).isEqualTo(2);
        var saved = ArgumentCaptor.forClass(NewsEvent.class);
        verify(events, times(1)).save(saved.capture());
        assertThat(saved.getValue().getParsedAt()).isEqualTo(NOW);
        assertThat(saved.getValue().isRaised()).isFalse();
        verify(currencies, times(1)).findByNameIgnoreCase("USD");
    }

    @Test
    void findNextQueriesBothLegsAndConvertsToBrokerTime() {
        var event = new NewsEvent(new Currency("EUR"), NOW.plusSeconds(1800), "CPI", Importance.HIGH, NOW);
        when(events.findUpcoming(any(), any(), any(), any(), any())).thenReturn(List.of(event));

        var next = service.findNext("EURUSD", NOW, Importance.MEDIUM);

        assertThat(next).hasValueSatisfying(upcoming -> {
            assertThat(upcoming.currency()).isEqualTo("EUR");
            assertThat(upcoming.brokerTime()).isEqualTo(java.time.LocalDateTime.parse("2026-10-31T12:30"));
        });
        assertThat(NewsEventService.currenciesOf("EUR/USD")).containsExactly("EUR", "USD");
        assertThat(NewsEventService.currenciesOf("XAU")).containsExactly("XAU");
    }

    private static ParsedNewsEvent event(String currency, Instant at, Importance importance) {
        return new ParsedNewsEvent(currency, "Event", importance, at, null, null, null);
    }
}
