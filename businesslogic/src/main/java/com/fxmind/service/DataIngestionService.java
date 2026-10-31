package com.fxmind.service;

import com.fxmind.dto.IngestionResult;
import com.fxmind.provider.DataProvider;
import com.fxmind.provider.NewsProvider;
import com.fxmind.provider.PositionRatioProvider;
import com.fxmind.provider.ProviderRegistry;
import org.springframework.stereotype.Service;

/**
 * Runs a {@link DataProvider} and routes its output to the matching ingestion use case. The provider
 * hierarchy is sealed, so the {@code switch} below is checked for exhaustiveness by the compiler.
 */
@Service
public class DataIngestionService {

    private final ProviderRegistry registry;
    private final NewsEventService newsEvents;
    private final SentimentService sentiments;

    public DataIngestionService(ProviderRegistry registry, NewsEventService newsEvents, SentimentService sentiments) {
        this.registry = registry;
        this.newsEvents = newsEvents;
        this.sentiments = sentiments;
    }

    public IngestionResult run(String providerKey) {
        return run(registry.require(providerKey));
    }

    public IngestionResult run(DataProvider provider) {
        return switch (provider) {
            case NewsProvider news -> newsEvents.ingest(news.key(), news.fetchEvents());
            case PositionRatioProvider ratios ->
                sentiments.ingest(ratios.key(), ratios.siteName(), ratios.fetchRatios());
        };
    }
}
