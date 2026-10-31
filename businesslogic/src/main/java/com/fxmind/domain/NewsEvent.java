package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;

/** Economic-calendar event (e.g. "Non-Farm Payrolls") scheduled for a currency. */
@Entity
@Table(name = "news_event", indexes = @Index(name = "IX_NEWS_EVENT_HAPPEN_TIME", columnList = "HAPPEN_TIME"))
public class NewsEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURRENCY_ID", nullable = false)
    private Currency currency;

    @Column(name = "HAPPEN_TIME", nullable = false)
    private Instant happenTime;

    @Column(name = "NAME", length = 500, nullable = false)
    private String name;

    @Column(name = "IMPORTANCE", nullable = false)
    private Importance importance;

    @Column(name = "ACTUAL_VALUE", length = 127)
    private String actualValue;

    @Column(name = "FORECAST_VALUE", length = 127)
    private String forecastValue;

    @Column(name = "PREVIOUS_VALUE", length = 127)
    private String previousValue;

    @Column(name = "PARSED_AT", nullable = false)
    private Instant parsedAt;

    @Column(name = "RAISED", nullable = false)
    private boolean raised;

    protected NewsEvent() {}

    public NewsEvent(Currency currency, Instant happenTime, String name, Importance importance, Instant parsedAt) {
        this.currency = Objects.requireNonNull(currency, "currency");
        this.happenTime = Objects.requireNonNull(happenTime, "happenTime");
        this.name = Objects.requireNonNull(name, "name");
        this.importance = Objects.requireNonNull(importance, "importance");
        this.parsedAt = Objects.requireNonNull(parsedAt, "parsedAt");
        this.raised = !happenTime.isAfter(parsedAt);
    }

    public NewsEvent withValues(String actual, String forecast, String previous) {
        this.actualValue = actual;
        this.forecastValue = forecast;
        this.previousValue = previous;
        return this;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Instant getHappenTime() {
        return happenTime;
    }

    public String getName() {
        return name;
    }

    public Importance getImportance() {
        return importance;
    }

    public String getActualValue() {
        return actualValue;
    }

    public String getForecastValue() {
        return forecastValue;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public Instant getParsedAt() {
        return parsedAt;
    }

    public boolean isRaised() {
        return raised;
    }
}
