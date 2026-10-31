package com.fxmind.domain;

import java.util.Arrays;
import java.util.Locale;

/** Expected market impact of an economic-calendar event. The level is part of the MT4/MT5 wire contract. */
public enum Importance {
    LOW(0),
    MEDIUM(1),
    HIGH(2);

    private final int level;

    Importance(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    public static Importance ofLevel(int level) {
        return Arrays.stream(values())
                .filter(importance -> importance.level == level)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown importance level: " + level));
    }

    /** Lenient parser for provider labels such as {@code "High"}, {@code "medium"}; unknown labels map to LOW. */
    public static Importance fromLabel(String label) {
        if (label == null) {
            return LOW;
        }
        return switch (label.strip().toLowerCase(Locale.ROOT)) {
            case "high" -> HIGH;
            case "medium" -> MEDIUM;
            default -> LOW;
        };
    }
}
