package com.fxmind.dto;

/** Aggregated crowd positioning; ratios are {@code null} when no samples fell in the window. */
public record SentimentAverage(Double longRatio, Double shortRatio, long samples) {

    public static final SentimentAverage EMPTY = new SentimentAverage(null, null, 0);

    public boolean isEmpty() {
        return samples == 0 || longRatio == null;
    }
}
