package com.fxmind.provider.sentiment;

import com.fxmind.domain.Symbol;
import com.fxmind.dto.PositionRatioSample;
import com.fxmind.exception.DataProviderException;
import com.fxmind.provider.PageFetcher;
import com.fxmind.provider.Percentages;
import com.fxmind.provider.PositionRatioProvider;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

/**
 * MyFXBook community outlook. Each symbol's figures live in a hidden tooltip ({@code #outlookTipN})
 * whose value is an embedded HTML table: row 1 = symbol, row 2 = short %, row 3 = long %.
 */
@Component
public class MyFxBookPositionRatioProvider implements PositionRatioProvider {

    static final URI OUTLOOK = URI.create("https://www.myfxbook.com/community/outlook");

    @Override
    public String key() {
        return "myfxbook";
    }

    @Override
    public String displayName() {
        return "MyFXBook community outlook";
    }

    @Override
    public String siteName() {
        return "MyFXBook";
    }

    private final PageFetcher fetcher;

    public MyFxBookPositionRatioProvider(PageFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public List<PositionRatioSample> fetchRatios() {
        return parse(Jsoup.parse(fetcher.fetch(key(), OUTLOOK), OUTLOOK.toString()));
    }

    List<PositionRatioSample> parse(Document document) {
        var table = document.getElementById("outlookSymbolsTable");
        if (table == null) {
            throw new DataProviderException(key(), "Outlook table not found - page layout changed?");
        }
        var samples = new ArrayList<PositionRatioSample>();
        for (int i = 0; ; i++) {
            var tooltip = table.getElementById("outlookTip" + i);
            if (tooltip == null) {
                break;
            }
            parseTooltip(tooltip).ifPresent(samples::add);
        }
        return samples;
    }

    private static Optional<PositionRatioSample> parseTooltip(Element tooltip) {
        var rows = Jsoup.parseBodyFragment(tooltip.val()).select("table tr");
        if (rows.size() < 4) {
            return Optional.empty();
        }
        var symbol = cell(rows.get(1), 0);
        var shortRatio = Percentages.parse(cell(rows.get(2), 1));
        var longRatio = Percentages.parse(cell(rows.get(3), 1));
        if (symbol.isBlank() || shortRatio.isEmpty() || longRatio.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new PositionRatioSample(
                Symbol.canonicalName(symbol), longRatio.getAsDouble(), shortRatio.getAsDouble()));
    }

    private static String cell(Element row, int index) {
        var cells = row.getElementsByTag("td");
        return cells.size() > index ? cells.get(index).text() : "";
    }
}
