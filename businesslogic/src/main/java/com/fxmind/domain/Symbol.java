package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Locale;

/** Tradable currency pair, stored in {@code BASE/QUOTE} notation (e.g. {@code EUR/USD}). */
@Entity
@Table(name = "symbol")
public class Symbol extends BaseEntity {

    public static final char SEPARATOR = '/';

    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "BASE_CURRENCY", length = 10, nullable = false)
    private String baseCurrency;

    @Column(name = "QUOTE_CURRENCY", length = 10, nullable = false)
    private String quoteCurrency;

    @Column(name = "DISABLED", nullable = false)
    private boolean disabled;

    @Column(name = "TRACK_SENTIMENT", nullable = false)
    private boolean trackSentiment = true;

    protected Symbol() {}

    public Symbol(String baseCurrency, String quoteCurrency) {
        this.baseCurrency = baseCurrency.toUpperCase(Locale.ROOT);
        this.quoteCurrency = quoteCurrency.toUpperCase(Locale.ROOT);
        this.name = this.baseCurrency + SEPARATOR + this.quoteCurrency;
    }

    /**
     * Normalises MetaTrader ({@code EURUSD}) and provider ({@code eur-usd}, {@code EUR/USD}) notations to
     * the canonical {@code EUR/USD} form.
     */
    public static String canonicalName(String raw) {
        var compact = raw.strip().toUpperCase(Locale.ROOT).replaceAll("[^A-Z]", "");
        if (compact.length() != 6) {
            return raw.strip().toUpperCase(Locale.ROOT);
        }
        return compact.substring(0, 3) + SEPARATOR + compact.substring(3);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isTrackSentiment() {
        return trackSentiment;
    }

    public void setTrackSentiment(boolean trackSentiment) {
        this.trackSentiment = trackSentiment;
    }
}
