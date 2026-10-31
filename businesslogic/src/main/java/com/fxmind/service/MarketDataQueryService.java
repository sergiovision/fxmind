package com.fxmind.service;

import com.fxmind.domain.Importance;
import com.fxmind.dto.SentimentAverage;
import com.fxmind.dto.UpcomingNewsEvent;
import com.fxmind.port.MarketDataQueryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** Application service behind the {@link MarketDataQueryPort}: converts broker time and delegates. */
@Service
public class MarketDataQueryService implements MarketDataQueryPort {

    private final NewsEventService newsEvents;
    private final SentimentService sentiments;
    private final BrokerClock brokerClock;

    public MarketDataQueryService(NewsEventService newsEvents, SentimentService sentiments, BrokerClock brokerClock) {
        this.newsEvents = newsEvents;
        this.sentiments = sentiments;
        this.brokerClock = brokerClock;
    }

    @Override
    public Optional<UpcomingNewsEvent> nextNewsEvent(String symbol, String brokerTime, int minImportance) {
        return newsEvents.findNext(
                symbol, brokerClock.parseBrokerTime(brokerTime), Importance.ofLevel(Math.clamp(minImportance, 0, 2)));
    }

    @Override
    public SentimentAverage currentSentiment(String symbol, String brokerTime) {
        return sentiments.current(symbol, brokerClock.parseBrokerTime(brokerTime));
    }

    @Override
    public List<Double> sentimentHistory(String symbol, List<String> brokerTimes, long siteId) {
        var barTimes = brokerTimes.stream().map(brokerClock::parseBrokerTime).toList();
        return sentiments.longRatioHistory(symbol, barTimes, siteId == 0 ? null : siteId);
    }

    @Override
    public String formatBrokerTime(UpcomingNewsEvent event) {
        return event.brokerTime().format(BrokerClock.MT_FORMAT);
    }
}
