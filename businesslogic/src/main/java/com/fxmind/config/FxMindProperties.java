package com.fxmind.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;
import java.time.ZoneId;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Type-safe application configuration bound from the {@code fxmind.*} namespace.
 *
 * <p>Replaces the former {@code fxmind.properties} file. Secrets are never stored here; they are
 * supplied through environment variables (see {@code .env.example}).
 */
@Validated
@ConfigurationProperties(prefix = "fxmind")
public record FxMindProperties(
        @Valid @NotNull @DefaultValue Broker broker,
        @Valid @NotNull @DefaultValue Thrift thrift,
        @Valid @NotNull @DefaultValue Mail mail,
        @Valid @NotNull @DefaultValue Http http,
        @Valid @NotNull @DefaultValue Sentiment sentiment) {

    /** Trading-server (broker) clock used by MetaTrader terminals. */
    public record Broker(@NotNull @DefaultValue("EET") ZoneId timeZone) {}

    /** Embedded Thrift server that MetaTrader expert advisors connect to. */
    public record Thrift(
            @DefaultValue("true") boolean enabled,
            @NotBlank @DefaultValue("0.0.0.0") String host,
            @Min(0) @Max(65535) @DefaultValue("2011") int port,
            @NotNull @DefaultValue("30s") Duration clientTimeout) {}

    /** Outgoing e-mail settings (SMTP credentials live in {@code spring.mail.*}). */
    public record Mail(
            @NotBlank @DefaultValue("fxmind-noreply@example.com")
            String from,

            @NotBlank @DefaultValue("FxHubJava registration")
            String registrationSubject,

            @NotNull @DefaultValue("http://localhost:8080/activate")
            URI registrationLinkBase) {}

    /** Outbound HTTP client used by data providers. */
    public record Http(
            @NotNull @DefaultValue("10s") Duration connectTimeout,
            @NotNull @DefaultValue("30s") Duration requestTimeout,

            @NotBlank @DefaultValue("Mozilla/5.0 (compatible; FxHubJava/1.0)")
            String userAgent) {}

    /** Crowd-sentiment aggregation windows. */
    public record Sentiment(
            @NotNull @DefaultValue("100m") Duration currentWindow,
            @NotNull @DefaultValue("1h") Duration historyWindow) {}
}
