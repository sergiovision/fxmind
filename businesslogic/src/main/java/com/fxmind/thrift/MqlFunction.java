package com.fxmind.thrift;

import java.util.Arrays;

/** Function names an MQL client may pass in the {@code func} parameter. */
enum MqlFunction {
    NEXT_NEWS_EVENT("NextNewsEvent"),
    CURRENT_SENTIMENTS("CurrentSentiments"),
    SENTIMENTS_ARRAY("SentimentsArray"),
    UNKNOWN("");

    private final String wireName;

    MqlFunction(String wireName) {
        this.wireName = wireName;
    }

    static MqlFunction from(String wireName) {
        return Arrays.stream(values())
                .filter(function -> function != UNKNOWN && function.wireName.equals(wireName))
                .findFirst()
                .orElse(UNKNOWN);
    }
}
