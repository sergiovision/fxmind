package com.fxmind.service;

import com.fxmind.config.FxMindProperties;
import com.fxmind.domain.OpenPositionRatio;
import com.fxmind.domain.Symbol;
import com.fxmind.dto.IngestionResult;
import com.fxmind.dto.PositionRatioSample;
import com.fxmind.dto.SentimentAverage;
import com.fxmind.exception.ResourceNotFoundException;
import com.fxmind.repository.OpenPositionRatioRepository;
import com.fxmind.repository.SiteRepository;
import com.fxmind.repository.SymbolRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Retail crowd-sentiment (open position ratio) ingestion and aggregation. */
@Service
@Transactional(readOnly = true)
public class SentimentService {

    /** Value returned to MQL clients for a bar without samples (part of the wire contract). */
    public static final double GAP_VALUE = com.fxmind.global.fxmindConstants.GAP_VALUE;

    private static final Logger log = LoggerFactory.getLogger(SentimentService.class);

    private final OpenPositionRatioRepository ratios;
    private final SymbolRepository symbols;
    private final SiteRepository sites;
    private final BrokerClock brokerClock;
    private final Duration currentWindow;
    private final Duration historyWindow;

    public SentimentService(
            OpenPositionRatioRepository ratios,
            SymbolRepository symbols,
            SiteRepository sites,
            BrokerClock brokerClock,
            FxMindProperties properties) {
        this.ratios = ratios;
        this.symbols = symbols;
        this.sites = sites;
        this.brokerClock = brokerClock;
        this.currentWindow = properties.sentiment().currentWindow();
        this.historyWindow = properties.sentiment().historyWindow();
    }

    @Transactional
    public IngestionResult ingest(String provider, String siteName, List<PositionRatioSample> samples) {
        var site = sites.findByName(siteName).orElseThrow(() -> new ResourceNotFoundException("Site", siteName));
        var parsedAt = brokerClock.now();
        var entities = samples.stream()
                .map(sample -> symbols.findByName(Symbol.canonicalName(sample.symbol()))
                        .map(symbol ->
                                new OpenPositionRatio(symbol, site, parsedAt, sample.longRatio(), sample.shortRatio()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        ratios.saveAll(entities);
        var result = new IngestionResult(provider, samples.size(), entities.size(), samples.size() - entities.size());
        log.info("Sentiment ingestion finished: {}", result.summary());
        return result;
    }

    /** Average positioning across all sites over the configured window ending at {@code at}. */
    public SentimentAverage current(String symbol, Instant at) {
        return ratios.average(Symbol.canonicalName(symbol), at.minus(currentWindow), at, null);
    }

    /**
     * Average long ratio per bar, one value for each bar close time; {@link #GAP_VALUE} marks bars
     * without data. {@code siteId == null} aggregates all sites.
     */
    public List<Double> longRatioHistory(String symbol, List<Instant> barTimes, Long siteId) {
        var canonical = Symbol.canonicalName(symbol);
        return barTimes.stream()
                .map(to -> ratios.average(canonical, to.minus(historyWindow), to, siteId))
                .map(average -> average.isEmpty() ? GAP_VALUE : average.longRatio())
                .toList();
    }
}
