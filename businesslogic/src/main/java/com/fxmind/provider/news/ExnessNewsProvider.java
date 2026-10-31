package com.fxmind.provider.news;

import com.fxmind.domain.Importance;
import com.fxmind.dto.ParsedNewsEvent;
import com.fxmind.provider.NewsProvider;
import com.fxmind.provider.PageFetcher;
import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Exness economic calendar (HTML table). Rows only repeat the date on the first event of a day, so
 * the current date is carried forward while walking the table. Times are published in UTC.
 */
@Component
public class ExnessNewsProvider implements NewsProvider {

    static final URI CALENDAR = URI.create("https://www.exness.com/intl/en/tools/calendar/");

    private static final Logger log = LoggerFactory.getLogger(ExnessNewsProvider.class);
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH);
    private static final String CELL = "td.economyCalendar-td__";

    private final PageFetcher fetcher;
    private final Clock clock;

    public ExnessNewsProvider(PageFetcher fetcher, Clock clock) {
        this.fetcher = fetcher;
        this.clock = clock;
    }

    @Override
    public String key() {
        return "exness";
    }

    @Override
    public String displayName() {
        return "Exness calendar";
    }

    @Override
    public List<ParsedNewsEvent> fetchEvents() {
        return parse(Jsoup.parse(fetcher.fetch(key(), CALENDAR), CALENDAR.toString()));
    }

    List<ParsedNewsEvent> parse(Document document) {
        var events = new ArrayList<ParsedNewsEvent>();
        var currentDay = LocalDate.now(clock);
        for (var row : document.select("table.economyCalendar-table tr")) {
            var day = text(row, 1).flatMap(this::parseDay);
            if (day.isPresent()) {
                currentDay = day.get();
            }
            var date = currentDay;
            var time = text(row, 2).flatMap(ExnessNewsProvider::parseTime);
            var currency = text(row, 3);
            var name = text(row, 4);
            if (time.isEmpty() || currency.isEmpty() || name.isEmpty()) {
                continue;
            }
            events.add(new ParsedNewsEvent(
                    currency.get().toUpperCase(Locale.ROOT),
                    name.get(),
                    Importance.fromLabel(text(row, 5).orElse(null)),
                    date.atTime(time.get()).toInstant(ZoneOffset.UTC),
                    text(row, 6).orElse(null),
                    text(row, 7).orElse(null),
                    text(row, 8).orElse(null)));
        }
        return events;
    }

    private Optional<LocalDate> parseDay(String text) {
        try {
            return Optional.of(nearestDate(MonthDay.parse(text, DAY_FORMAT), LocalDate.now(clock)));
        } catch (DateTimeParseException e) {
            log.debug("Unrecognised Exness day header '{}'", text);
            return Optional.empty();
        }
    }

    /**
     * Headers carry no year; picks the year that puts the date closest to today so calendars spanning
     * New Year resolve correctly.
     */
    static LocalDate nearestDate(MonthDay day, LocalDate today) {
        return java.util.stream.IntStream.rangeClosed(today.getYear() - 1, today.getYear() + 1)
                .mapToObj(day::atYear)
                .min(java.util.Comparator.comparingLong(
                        candidate -> Math.abs(java.time.temporal.ChronoUnit.DAYS.between(today, candidate))))
                .orElseThrow();
    }

    private static Optional<LocalTime> parseTime(String text) {
        try {
            return Optional.of(LocalTime.parse(text, TIME_FORMAT));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    private static Optional<String> text(Element row, int column) {
        return Optional.ofNullable(row.selectFirst(CELL + column))
                .map(Element::text)
                .map(String::strip)
                .filter(value -> !value.isEmpty());
    }
}
