package com.fxmind.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fxmind.domain.Importance;
import com.fxmind.domain.NewsEvent;
import com.fxmind.domain.OpenPositionRatio;
import com.fxmind.domain.Person;
import com.fxmind.domain.Privilege;
import com.fxmind.domain.Registration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository contract tests executed against every supported database. The schema comes exclusively
 * from Flyway ({@code ddl-auto=validate}), so these tests also verify the migrations match the
 * entity mappings.
 */
abstract class AbstractRepositoryTest {

    static final Instant NOW = Instant.parse("2026-10-31T10:00:00Z");

    @Autowired
    CurrencyRepository currencies;

    @Autowired
    SymbolRepository symbols;

    @Autowired
    SiteRepository sites;

    @Autowired
    NewsEventRepository events;

    @Autowired
    OpenPositionRatioRepository ratios;

    @Autowired
    PersonRepository persons;

    @Autowired
    JobDefinitionRepository jobs;

    @Autowired
    SettingRepository settings;

    @Test
    void seedDataIsLoadedByFlyway() {
        assertThat(currencies.findByNameIgnoreCase("eur")).isPresent();
        assertThat(symbols.findByName("EUR/USD")).isPresent();
        assertThat(symbols.findByDisabledFalseAndTrackSentimentTrue()).hasSize(28);
        assertThat(sites.findByName("OANDA")).isPresent();
        assertThat(jobs.findByEnabledTrue()).extracting("handler").contains("forexfactory", "oanda", "myfxbook");
        assertThat(settings.findByName("db.version")).isPresent();
    }

    @Test
    void findsUpcomingEventsByCurrencyWindowAndImportance() {
        var usd = currencies.findByNameIgnoreCase("USD").orElseThrow();
        var eur = currencies.findByNameIgnoreCase("EUR").orElseThrow();
        events.save(new NewsEvent(usd, NOW.plusSeconds(7200), "NFP", Importance.HIGH, NOW));
        events.save(new NewsEvent(eur, NOW.plusSeconds(3600), "CPI", Importance.LOW, NOW));
        events.save(new NewsEvent(eur, NOW.plusSeconds(5400), "ECB", Importance.MEDIUM, NOW));
        events.flush();

        var next = events.findUpcoming(
                List.of("EUR", "USD"),
                NOW,
                NOW.plusSeconds(86_400),
                List.of(Importance.MEDIUM, Importance.HIGH),
                Limit.of(1));

        assertThat(next).singleElement().extracting(NewsEvent::getName).isEqualTo("ECB");
        assertThat(events.existsByCurrencyAndHappenTimeAndImportance(usd, NOW.plusSeconds(7200), Importance.HIGH))
                .isTrue();
        assertThat(events.findByHappenTimeGreaterThanEqual(NOW, PageRequest.of(0, 10)))
                .hasSize(3);
    }

    @Test
    void averagesSentimentPerSymbolWindowAndSite() {
        var eurUsd = symbols.findByName("EUR/USD").orElseThrow();
        var oanda = sites.findByName("OANDA").orElseThrow();
        var myFxBook = sites.findByName("MyFXBook").orElseThrow();
        ratios.save(new OpenPositionRatio(eurUsd, oanda, NOW.minusSeconds(600), 40, 60));
        ratios.save(new OpenPositionRatio(eurUsd, myFxBook, NOW.minusSeconds(300), 60, 40));
        ratios.save(new OpenPositionRatio(eurUsd, oanda, NOW.minusSeconds(86_400), 90, 10));
        ratios.flush();

        var all = ratios.average("EUR/USD", NOW.minusSeconds(3600), NOW, null);
        var oandaOnly = ratios.average("EUR/USD", NOW.minusSeconds(3600), NOW, oanda.getId());
        var none = ratios.average("GBP/USD", NOW.minusSeconds(3600), NOW, null);

        assertThat(all.longRatio()).isEqualTo(50.0);
        assertThat(all.samples()).isEqualTo(2);
        assertThat(oandaOnly.longRatio()).isEqualTo(40.0);
        assertThat(none.isEmpty()).isTrue();
    }

    @Test
    @Transactional // the admin look-up takes a pessimistic lock, which requires a transaction
    void findsPersonsByEmailAndActivationToken() {
        persons.saveAndFlush(new Person(
                "trader@example.com", "{bcrypt}hash", Privilege.FREE, new Registration("tok-1", NOW, "127.0.0.1")));

        assertThat(persons.findByEmailIgnoreCase("TRADER@example.com")).isPresent();
        assertThat(persons.findByRegistrationActivationToken("tok-1")).isPresent();
        assertThat(persons.existsByPrivilege(Privilege.ADMIN)).isFalse();
        assertThat(persons.findByPrivilegeAndRegistrationActivatedTrue(Privilege.ADMIN))
                .isEmpty();
    }
}
