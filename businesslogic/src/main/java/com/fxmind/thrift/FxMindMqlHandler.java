package com.fxmind.thrift;

import com.fxmind.global.FXMindMQL;
import com.fxmind.port.MarketDataQueryPort;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Thrift adapter (Ports &amp; Adapters): translates the generic, string-keyed MQL RPC contract into
 * calls on the {@link MarketDataQueryPort}. All failures are logged and answered with an empty result,
 * because MQL clients cannot handle Thrift exceptions gracefully.
 */
@Component
public class FxMindMqlHandler implements FXMindMQL.Iface {

    static final String FUNC = "func";

    private static final Logger log = LoggerFactory.getLogger(FxMindMqlHandler.class);
    private static final double NO_SENTIMENT = -1.0;

    private final MarketDataQueryPort marketData;

    public FxMindMqlHandler(MarketDataQueryPort marketData) {
        this.marketData = marketData;
    }

    @Override
    public List<String> ProcessStringData(Map<String, String> params, List<String> inputData) {
        try {
            return switch (MqlFunction.from(params.get(FUNC))) {
                case NEXT_NEWS_EVENT ->
                    marketData
                            .nextNewsEvent(
                                    required(params, "symbol"),
                                    required(params, "time"),
                                    Integer.parseInt(required(params, "importance")))
                            .map(event -> List.of(
                                    event.currency(),
                                    Integer.toString(event.importance().level()),
                                    marketData.formatBrokerTime(event),
                                    event.name()))
                            .orElse(List.of());
                default -> unsupported("ProcessStringData", params);
            };
        } catch (RuntimeException e) {
            log.error("ProcessStringData failed for {}", params, e);
            return List.of();
        }
    }

    @Override
    public List<Double> ProcessDoubleData(Map<String, String> params, List<String> inputData) {
        try {
            return switch (MqlFunction.from(params.get(FUNC))) {
                case CURRENT_SENTIMENTS -> {
                    var average = marketData.currentSentiment(required(params, "symbol"), required(params, "time"));
                    yield average.isEmpty()
                            ? List.of(NO_SENTIMENT, NO_SENTIMENT)
                            : List.of(average.longRatio(), average.shortRatio());
                }
                case SENTIMENTS_ARRAY ->
                    marketData.sentimentHistory(
                            required(params, "symbol"),
                            inputData == null ? List.of() : inputData,
                            Long.parseLong(params.getOrDefault("site", "0")));
                default -> unsupported("ProcessDoubleData", params);
            };
        } catch (RuntimeException e) {
            log.error("ProcessDoubleData failed for {}", params, e);
            return List.of();
        }
    }

    @Override
    public long IsServerActive(Map<String, String> params) {
        return 1L;
    }

    @Override
    public void PostStatusMessage(Map<String, String> params) {
        log.info(
                "MQL status [account={}, magic={}]: {}",
                params.get("account"),
                params.get("magic"),
                params.get("message"));
    }

    private static String required(Map<String, String> params, String name) {
        var value = params.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing parameter '" + name + "'");
        }
        return value;
    }

    private static <T> List<T> unsupported(String method, Map<String, String> params) {
        log.warn("{}: unsupported function '{}'", method, params.get(FUNC));
        return List.of();
    }
}
