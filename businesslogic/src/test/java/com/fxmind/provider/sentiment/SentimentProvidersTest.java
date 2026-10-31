package com.fxmind.provider.sentiment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fxmind.Fixtures;
import com.fxmind.domain.Symbol;
import com.fxmind.dto.PositionRatioSample;
import com.fxmind.exception.DataProviderException;
import com.fxmind.repository.SymbolRepository;
import java.net.URI;
import java.util.List;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class SentimentProvidersTest {

    @Test
    void myFxBookReadsTooltipTablesAndIgnoresBrokenOnes() {
        var provider = new MyFxBookPositionRatioProvider((name, uri) -> Fixtures.load("myfxbook-outlook.html"));

        assertThat(provider.fetchRatios())
                .containsExactly(
                        new PositionRatioSample("EUR/USD", 38, 62), new PositionRatioSample("USD/JPY", 55, 45));
    }

    @Test
    void myFxBookFailsLoudlyWhenLayoutChanges() {
        var provider = new MyFxBookPositionRatioProvider((name, uri) -> "<html></html>");

        assertThatThrownBy(provider::fetchRatios).isInstanceOf(DataProviderException.class);
    }

    @Test
    void oandaDerivesMissingSideAndSkipsEmptyItems() {
        var provider = new OandaPositionRatioProvider((name, uri) -> Fixtures.load("oanda-ratios.html"));

        assertThat(provider.parse(Jsoup.parse(Fixtures.load("oanda-ratios.html"))))
                .containsExactly(
                        new PositionRatioSample("EUR/USD", 41.5, 58.5), new PositionRatioSample("GBP/USD", 70, 30));
    }

    @Test
    void eToroQueriesSupportedSymbolsOnly() {
        var symbols = mock(SymbolRepository.class);
        when(symbols.findByDisabledFalseAndTrackSentimentTrue())
                .thenReturn(List.of(new Symbol("EUR", "USD"), new Symbol("GBP", "CHF")));
        var requested = new java.util.ArrayList<URI>();
        var provider = new EToroPositionRatioProvider(
                (name, uri) -> {
                    requested.add(uri);
                    return Fixtures.load("etoro-eur-usd.json");
                },
                new JsonMapper(),
                symbols);

        var ratios = provider.fetchRatios();

        assertThat(requested).containsExactly(URI.create(EToroPositionRatioProvider.ENDPOINT + "eur-usd"));
        assertThat(ratios).containsExactly(new PositionRatioSample("EUR/USD", 47, 53));
    }
}
