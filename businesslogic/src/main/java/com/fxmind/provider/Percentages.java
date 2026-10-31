package com.fxmind.provider;

import java.util.OptionalDouble;

/** Parsing helpers for percentage strings scraped from HTML ("53 %", "47,5%", "&nbsp;"). */
public final class Percentages {

    private Percentages() {}

    public static OptionalDouble parse(String text) {
        if (text == null) {
            return OptionalDouble.empty();
        }
        var cleaned =
                text.replace('\u00A0', ' ').replace("%", "").replace(',', '.').strip();
        if (cleaned.isEmpty()) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(cleaned));
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }
}
