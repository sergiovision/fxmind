package com.fxmind.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fxmind.dto.IngestionResult;
import com.fxmind.dto.ParsedNewsEvent;
import com.fxmind.dto.PositionRatioSample;
import com.fxmind.provider.NewsProvider;
import com.fxmind.provider.PositionRatioProvider;
import com.fxmind.provider.ProviderRegistry;
import java.util.List;
import org.junit.jupiter.api.Test;

class DataIngestionServiceTest {

    private final NewsEventService news = mock(NewsEventService.class);
    private final SentimentService sentiment = mock(SentimentService.class);

    private final NewsProvider newsProvider = new NewsProvider() {
        @Override
        public List<ParsedNewsEvent> fetchEvents() {
            return List.of();
        }

        @Override
        public String key() {
            return "news";
        }

        @Override
        public String displayName() {
            return "News";
        }
    };

    private final PositionRatioProvider ratioProvider = new PositionRatioProvider() {
        @Override
        public String siteName() {
            return "Site";
        }

        @Override
        public List<PositionRatioSample> fetchRatios() {
            return List.of(new PositionRatioSample("EUR/USD", 50, 50));
        }

        @Override
        public String key() {
            return "ratio";
        }

        @Override
        public String displayName() {
            return "Ratio";
        }
    };

    private final DataIngestionService service =
            new DataIngestionService(new ProviderRegistry(List.of(newsProvider, ratioProvider)), news, sentiment);

    @Test
    void routesEachProviderTypeToItsUseCase() {
        when(news.ingest("news", List.of())).thenReturn(new IngestionResult("news", 0, 0, 0));
        when(sentiment.ingest("ratio", "Site", ratioProvider.fetchRatios()))
                .thenReturn(new IngestionResult("ratio", 1, 1, 0));

        assertThat(service.run("news").provider()).isEqualTo("news");
        assertThat(service.run("ratio").stored()).isEqualTo(1);
        verify(sentiment).ingest("ratio", "Site", ratioProvider.fetchRatios());
    }

    @Test
    void registryRejectsUnknownAndDuplicateKeys() {
        assertThatThrownBy(() -> service.run("missing")).hasMessageContaining("Unknown data provider");
        assertThatThrownBy(() -> new ProviderRegistry(List.of(newsProvider, newsProvider)))
                .isInstanceOf(IllegalStateException.class);
    }
}
