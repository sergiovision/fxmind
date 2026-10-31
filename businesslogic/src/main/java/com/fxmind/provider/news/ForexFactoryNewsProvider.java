package com.fxmind.provider.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fxmind.domain.Importance;
import com.fxmind.dto.ParsedNewsEvent;
import com.fxmind.exception.DataProviderException;
import com.fxmind.provider.NewsProvider;
import com.fxmind.provider.PageFetcher;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * ForexFactory weekly calendar via its public JSON export. Timestamps carry an explicit UTC offset, so
 * no time-zone guessing is required.
 */
@Component
public class ForexFactoryNewsProvider implements NewsProvider {

    static final URI FEED = URI.create("https://nfs.faireconomy.media/ff_calendar_thisweek.json");

    /** ForexFactory tags global events (e.g. bank holidays) as "All"; they are attributed to USD. */
    private static final String ALL_CURRENCIES = "ALL";

    private static final String FALLBACK_CURRENCY = "USD";

    private final PageFetcher fetcher;
    private final JsonMapper jsonMapper;

    public ForexFactoryNewsProvider(PageFetcher fetcher, JsonMapper jsonMapper) {
        this.fetcher = fetcher;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public String key() {
        return "forexfactory";
    }

    @Override
    public String displayName() {
        return "ForexFactory calendar";
    }

    @Override
    public List<ParsedNewsEvent> fetchEvents() {
        return parse(fetcher.fetch(key(), FEED));
    }

    List<ParsedNewsEvent> parse(String json) {
        try {
            return Arrays.stream(jsonMapper.readValue(json, FeedItem[].class))
                    .filter(item -> item.title() != null && item.country() != null && item.date() != null)
                    .filter(item -> !"holiday".equalsIgnoreCase(item.impact()))
                    .map(FeedItem::toParsedEvent)
                    .toList();
        } catch (JacksonException e) {
            throw new DataProviderException(key(), "Malformed calendar feed", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record FeedItem(
            String title,
            String country,
            OffsetDateTime date,
            String impact,
            String actual,
            String forecast,
            String previous) {

        ParsedNewsEvent toParsedEvent() {
            var currency = country.strip().toUpperCase(Locale.ROOT);
            return new ParsedNewsEvent(
                    Objects.equals(currency, ALL_CURRENCIES) ? FALLBACK_CURRENCY : currency,
                    title.strip(),
                    Importance.fromLabel(impact),
                    date.toInstant(),
                    blankToNull(actual),
                    blankToNull(forecast),
                    blankToNull(previous));
        }

        private static String blankToNull(String value) {
            return value == null || value.isBlank() ? null : value.strip();
        }
    }
}
