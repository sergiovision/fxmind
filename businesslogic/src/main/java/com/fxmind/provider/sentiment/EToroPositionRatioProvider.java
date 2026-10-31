package com.fxmind.provider.sentiment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fxmind.domain.Symbol;
import com.fxmind.dto.PositionRatioSample;
import com.fxmind.exception.DataProviderException;
import com.fxmind.provider.PageFetcher;
import com.fxmind.provider.PositionRatioProvider;
import com.fxmind.repository.SymbolRepository;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/** eToro OpenBook instrument sentiment, queried once per tracked symbol. */
@Component
public class EToroPositionRatioProvider implements PositionRatioProvider {

    static final String ENDPOINT = "https://openbook.etoro.com/API/Markets/Symbol/InstrumentRate/?name=";

    /** Crosses eToro does not publish sentiment for. */
    static final Set<String> UNSUPPORTED = Set.of(
            "GBP/CHF", "AUD/NZD", "AUD/CAD", "AUD/CHF", "CAD/CHF", "EUR/NZD", "GBP/AUD", "GBP/CAD", "GBP/NZD",
            "NZD/CAD", "NZD/CHF", "NZD/JPY");

    private static final Logger log = LoggerFactory.getLogger(EToroPositionRatioProvider.class);

    private final PageFetcher fetcher;
    private final JsonMapper jsonMapper;
    private final SymbolRepository symbols;

    public EToroPositionRatioProvider(PageFetcher fetcher, JsonMapper jsonMapper, SymbolRepository symbols) {
        this.fetcher = fetcher;
        this.jsonMapper = jsonMapper;
        this.symbols = symbols;
    }

    @Override
    public String key() {
        return "etoro";
    }

    @Override
    public String displayName() {
        return "eToro OpenBook sentiment";
    }

    @Override
    public String siteName() {
        return "eToro";
    }

    @Override
    public List<PositionRatioSample> fetchRatios() {
        var samples = new ArrayList<PositionRatioSample>();
        for (var symbol : symbols.findByDisabledFalseAndTrackSentimentTrue()) {
            if (UNSUPPORTED.contains(symbol.getName())) {
                continue;
            }
            try {
                parse(symbol.getName(), fetcher.fetch(key(), uriFor(symbol))).ifPresent(samples::add);
            } catch (DataProviderException e) {
                log.warn("Skipping {}: {}", symbol.getName(), e.getMessage());
            }
        }
        return samples;
    }

    static URI uriFor(Symbol symbol) {
        return URI.create(ENDPOINT
                + symbol.getBaseCurrency().toLowerCase(Locale.ROOT)
                + "-"
                + symbol.getQuoteCurrency().toLowerCase(Locale.ROOT));
    }

    Optional<PositionRatioSample> parse(String symbol, String json) {
        try {
            var rate = jsonMapper.readValue(json, InstrumentRate.class);
            if (rate.sentimentPercent() == null || rate.sentimentType() == null) {
                return Optional.empty();
            }
            var type = rate.sentimentType().toLowerCase(Locale.ROOT);
            if (type.startsWith("buy")) {
                return Optional.of(PositionRatioSample.fromLong(symbol, rate.sentimentPercent()));
            }
            if (type.startsWith("sell")) {
                return Optional.of(PositionRatioSample.fromShort(symbol, rate.sentimentPercent()));
            }
            return Optional.empty();
        } catch (JacksonException e) {
            throw new DataProviderException(key(), "Malformed sentiment payload for " + symbol, e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record InstrumentRate(
            @JsonProperty("SentimentPercent") Double sentimentPercent,
            @JsonProperty("SentimentType") String sentimentType) {}
}
