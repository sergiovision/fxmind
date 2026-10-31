package com.fxmind.dto;

import java.util.Objects;

/** One long/short positioning reading (percentages) for a symbol in canonical {@code EUR/USD} form. */
public record PositionRatioSample(String symbol, double longRatio, double shortRatio) {

    public PositionRatioSample {
        Objects.requireNonNull(symbol, "symbol");
        if (longRatio < 0 || shortRatio < 0) {
            throw new IllegalArgumentException("Ratios must be non-negative: " + longRatio + "/" + shortRatio);
        }
    }

    /** Builds a sample from one side only, deriving the other as the complement to 100%. */
    public static PositionRatioSample fromLong(String symbol, double longRatio) {
        return new PositionRatioSample(symbol, longRatio, 100.0 - longRatio);
    }

    public static PositionRatioSample fromShort(String symbol, double shortRatio) {
        return new PositionRatioSample(symbol, 100.0 - shortRatio, shortRatio);
    }
}
