package com.fxmind.port;

import com.fxmind.dto.SentimentAverage;
import com.fxmind.dto.UpcomingNewsEvent;
import java.util.List;
import java.util.Optional;

/**
 * Inbound application port used by transport adapters (Thrift today). Timestamps are MetaTrader
 * broker-time strings in {@code yyyy.MM.dd HH:mm} format, exactly as sent by MQL clients.
 */
public interface MarketDataQueryPort {

    Optional<UpcomingNewsEvent> nextNewsEvent(String symbol, String brokerTime, int minImportance);

    SentimentAverage currentSentiment(String symbol, String brokerTime);

    /** One average long ratio per bar time; {@code siteId == 0} aggregates all sites. */
    List<Double> sentimentHistory(String symbol, List<String> brokerTimes, long siteId);

    String formatBrokerTime(UpcomingNewsEvent event);
}
