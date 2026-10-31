package com.fxmind.provider.sentiment;

import com.fxmind.domain.Symbol;
import com.fxmind.dto.PositionRatioSample;
import com.fxmind.exception.DataProviderException;
import com.fxmind.provider.PageFetcher;
import com.fxmind.provider.Percentages;
import com.fxmind.provider.PositionRatioProvider;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

/** OANDA open-position ratios: one {@code <li>} per instrument with long and short bars. */
@Component
public class OandaPositionRatioProvider implements PositionRatioProvider {

    static final URI RATIOS = URI.create("https://fxtrade.oanda.com/analysis/open-position-ratios");

    private final PageFetcher fetcher;

    public OandaPositionRatioProvider(PageFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public String key() {
        return "oanda";
    }

    @Override
    public String displayName() {
        return "OANDA open-position ratios";
    }

    @Override
    public String siteName() {
        return "OANDA";
    }

    @Override
    public List<PositionRatioSample> fetchRatios() {
        return parse(Jsoup.parse(fetcher.fetch(key(), RATIOS), RATIOS.toString()));
    }

    List<PositionRatioSample> parse(Document document) {
        var items = document.select("ol#long-short-ratio-graph > li");
        if (items.isEmpty()) {
            throw new DataProviderException(key(), "Ratio list not found - page layout changed?");
        }
        return items.stream()
                .map(OandaPositionRatioProvider::toSample)
                .filter(Objects::nonNull)
                .toList();
    }

    private static PositionRatioSample toSample(Element item) {
        var name = item.selectFirst("span");
        var bars = item.getElementsByTag("div");
        if (name == null || bars.size() < 2) {
            return null;
        }
        var longRatio = percentageIn(bars.get(0));
        var shortRatio = percentageIn(bars.get(1));
        var symbol = Symbol.canonicalName(name.text());
        if (longRatio.isPresent() && shortRatio.isPresent()) {
            return new PositionRatioSample(symbol, longRatio.getAsDouble(), shortRatio.getAsDouble());
        }
        if (longRatio.isPresent()) {
            return PositionRatioSample.fromLong(symbol, longRatio.getAsDouble());
        }
        return shortRatio.isPresent() ? PositionRatioSample.fromShort(symbol, shortRatio.getAsDouble()) : null;
    }

    private static OptionalDouble percentageIn(Element bar) {
        return bar.getElementsByTag("span").stream()
                .map(span -> Percentages.parse(span.text()))
                .filter(OptionalDouble::isPresent)
                .findFirst()
                .orElse(OptionalDouble.empty());
    }
}
