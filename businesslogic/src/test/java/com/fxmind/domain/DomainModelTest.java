package com.fxmind.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fxmind.dto.PositionRatioSample;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DomainModelTest {

    @ParameterizedTest
    @CsvSource({"EURUSD,EUR/USD", "eur-usd,EUR/USD", " GBP/JPY ,GBP/JPY", "XAU,XAU"})
    void canonicalisesSymbolNotations(String raw, String expected) {
        assertThat(Symbol.canonicalName(raw)).isEqualTo(expected);
    }

    @Test
    void importanceRoundTripsThroughWireLevel() {
        for (var importance : Importance.values()) {
            assertThat(Importance.ofLevel(importance.level())).isEqualTo(importance);
        }
        assertThat(Importance.fromLabel("High")).isEqualTo(Importance.HIGH);
        assertThat(Importance.fromLabel("unknown")).isEqualTo(Importance.LOW);
        assertThatThrownBy(() -> Importance.ofLevel(7)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void newsEventIsRaisedWhenParsedAfterItHappened() {
        var usd = new Currency("USD");
        var now = Instant.parse("2026-10-31T12:00:00Z");

        assertThat(new NewsEvent(usd, now.minusSeconds(60), "CPI", Importance.HIGH, now).isRaised())
                .isTrue();
        assertThat(new NewsEvent(usd, now.plusSeconds(60), "CPI", Importance.HIGH, now).isRaised())
                .isFalse();
    }

    @Test
    void settingCanBeResetToItsDefault() {
        var setting = new Setting("key", "1", "test");
        setting.setValue("2");

        assertThat(setting.resetToDefault()).isTrue();
        assertThat(setting.getValue()).isEqualTo("1");
        assertThat(setting.resetToDefault()).isFalse();
    }

    @Test
    void jobStatusIsTruncatedToColumnLimit() {
        var job = new JobDefinition("News", "FF", "forexfactory", "0 0 * * * ?", null);

        job.recordExecution("x".repeat(JobDefinition.MAX_STATUS_LENGTH + 10), Instant.EPOCH, null);

        assertThat(job.getLastStatus()).hasSize(JobDefinition.MAX_STATUS_LENGTH);
    }

    @Test
    void positionRatioSampleDerivesComplement() {
        assertThat(PositionRatioSample.fromLong("EUR/USD", 40)).isEqualTo(new PositionRatioSample("EUR/USD", 40, 60));
        assertThat(PositionRatioSample.fromShort("EUR/USD", 40)).isEqualTo(new PositionRatioSample("EUR/USD", 60, 40));
        assertThatThrownBy(() -> new PositionRatioSample("EUR/USD", -1, 101))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void entityEqualityIsIdentifierBased() {
        assertThat(new Currency("EUR")).isNotEqualTo(new Currency("EUR"));
        assertThat(new Currency("EUR").hashCode()).isEqualTo(new Currency("USD").hashCode());
    }
}
