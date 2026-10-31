package com.fxmind.provider;

import com.fxmind.config.FxMindProperties;
import com.fxmind.exception.DataProviderException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.stereotype.Component;

/** {@link PageFetcher} backed by the JDK {@link HttpClient}. */
@Component
public class HttpClientPageFetcher implements PageFetcher {

    private final HttpClient httpClient;
    private final Duration requestTimeout;
    private final String userAgent;

    public HttpClientPageFetcher(HttpClient httpClient, FxMindProperties properties) {
        this.httpClient = httpClient;
        this.requestTimeout = properties.http().requestTimeout();
        this.userAgent = properties.http().userAgent();
    }

    @Override
    public String fetch(String provider, URI uri) {
        var request = HttpRequest.newBuilder(uri)
                .timeout(requestTimeout)
                .header("User-Agent", userAgent)
                .header("Accept", "text/html,application/json,application/xml;q=0.9,*/*;q=0.8")
                .GET()
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new DataProviderException(provider, "HTTP " + response.statusCode() + " from " + uri);
            }
            return response.body();
        } catch (IOException e) {
            throw new DataProviderException(provider, "I/O error fetching " + uri, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DataProviderException(provider, "Interrupted while fetching " + uri, e);
        }
    }
}
