package com.fxmind.config;

import java.net.http.HttpClient;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Infrastructure beans shared by every runtime (web console, tests, future workers). */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FxMindProperties.class)
public class CoreConfiguration {

    /** Injectable clock so time-dependent logic is deterministic under test. */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    HttpClient httpClient(FxMindProperties properties) {
        return HttpClient.newBuilder()
                .connectTimeout(properties.http().connectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /** Delegating encoder: BCrypt for new hashes while still recognising prefixed legacy formats. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
