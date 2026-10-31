package com.fxmind.web.ui;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/** Display formatting shared by grids. All times in the console are shown in UTC. */
public final class Formats {

    private static final DateTimeFormatter UTC =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm 'UTC'").withZone(ZoneOffset.UTC);

    private Formats() {}

    public static String utc(Instant instant) {
        return instant == null ? "" : UTC.format(instant);
    }
}
