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

/** Snapshot of retail long/short positioning for a symbol as published by one {@link Site}. */
@Entity
@Table(name = "open_position_ratio", indexes = @Index(name = "IX_OPR_SYMBOL_TIME", columnList = "SYMBOL_ID, PARSED_AT"))
public class OpenPositionRatio extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SYMBOL_ID", nullable = false)
    private Symbol symbol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SITE_ID", nullable = false)
    private Site site;

    @Column(name = "PARSED_AT", nullable = false)
    private Instant parsedAt;

    @Column(name = "LONG_RATIO", nullable = false)
    private double longRatio;

    @Column(name = "SHORT_RATIO", nullable = false)
    private double shortRatio;

    protected OpenPositionRatio() {}

    public OpenPositionRatio(Symbol symbol, Site site, Instant parsedAt, double longRatio, double shortRatio) {
        this.symbol = Objects.requireNonNull(symbol, "symbol");
        this.site = Objects.requireNonNull(site, "site");
        this.parsedAt = Objects.requireNonNull(parsedAt, "parsedAt");
        this.longRatio = longRatio;
        this.shortRatio = shortRatio;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Site getSite() {
        return site;
    }

    public Instant getParsedAt() {
        return parsedAt;
    }

    public double getLongRatio() {
        return longRatio;
    }

    public double getShortRatio() {
        return shortRatio;
    }
}
