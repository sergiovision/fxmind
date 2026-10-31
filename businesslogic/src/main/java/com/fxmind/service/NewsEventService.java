package com.fxmind.service;

import com.fxmind.domain.Currency;
import com.fxmind.domain.Importance;
import com.fxmind.domain.NewsEvent;
import com.fxmind.dto.IngestionResult;
import com.fxmind.dto.NewsEventDto;
import com.fxmind.dto.ParsedNewsEvent;
import com.fxmind.dto.UpcomingNewsEvent;
import com.fxmind.mapper.DomainMapper;
import com.fxmind.repository.CurrencyRepository;
import com.fxmind.repository.NewsEventRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Economic-calendar use cases: ingestion from providers and look-ups for clients and the console. */
@Service
@Transactional(readOnly = true)
public class NewsEventService {

    private static final Logger log = LoggerFactory.getLogger(NewsEventService.class);
    private static final Duration LOOKAHEAD = Duration.ofDays(1);

    private final NewsEventRepository events;
    private final CurrencyRepository currencies;
    private final BrokerClock brokerClock;
    private final DomainMapper mapper;

    public NewsEventService(
            NewsEventRepository events, CurrencyRepository currencies, BrokerClock brokerClock, DomainMapper mapper) {
        this.events = events;
        this.currencies = currencies;
        this.brokerClock = brokerClock;
        this.mapper = mapper;
    }

    /**
     * Persists new events, skipping duplicates (same currency, minute and importance) and events for
     * currencies the platform does not track.
     */
    @Transactional
    public IngestionResult ingest(String provider, List<ParsedNewsEvent> parsed) {
        var parsedAt = brokerClock.now();
        Map<String, Optional<Currency>> currencyCache = new HashMap<>();
        int stored = 0;
        for (var item : parsed) {
            var currency = currencyCache.computeIfAbsent(item.currency(), currencies::findByNameIgnoreCase);
            if (currency.isEmpty()
                    || events.existsByCurrencyAndHappenTimeAndImportance(
                            currency.get(), item.happenTime(), item.importance())) {
                continue;
            }
            events.save(new NewsEvent(currency.get(), item.happenTime(), item.name(), item.importance(), parsedAt)
                    .withValues(item.actualValue(), item.forecastValue(), item.previousValue()));
            stored++;
        }
        var result = new IngestionResult(provider, parsed.size(), stored, parsed.size() - stored);
        log.info("News ingestion finished: {}", result.summary());
        return result;
    }

    /**
     * Finds the first event within 24h after {@code from} affecting either leg of the symbol with at
     * least the given importance.
     */
    public Optional<UpcomingNewsEvent> findNext(String symbol, Instant from, Importance minImportance) {
        return events
                .findUpcoming(currenciesOf(symbol), from, from.plus(LOOKAHEAD), atLeast(minImportance), Limit.of(1))
                .stream()
                .findFirst()
                .map(event -> new UpcomingNewsEvent(
                        event.getCurrency().getName(),
                        event.getName(),
                        event.getImportance(),
                        brokerClock.toBrokerTime(event.getHappenTime())));
    }

    public Slice<NewsEventDto> upcoming(Pageable pageable) {
        return events.findByHappenTimeGreaterThanEqual(brokerClock.now(), pageable)
                .map(mapper::toDto);
    }

    public long countUpcoming() {
        return events.countByHappenTimeGreaterThanEqual(brokerClock.now());
    }

    static Collection<String> currenciesOf(String symbol) {
        var compact = symbol.strip().replaceAll("[^A-Za-z]", "");
        return compact.length() >= 6
                ? List.of(compact.substring(0, 3), compact.substring(3, 6))
                : List.of(compact.substring(0, Math.min(3, compact.length())));
    }

    private static List<Importance> atLeast(Importance minimum) {
        return Arrays.stream(Importance.values())
                .filter(importance -> importance.level() >= minimum.level())
                .toList();
    }
}
