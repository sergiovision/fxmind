package com.fxmind.provider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Registry of all {@link DataProvider} strategies, keyed by {@link DataProvider#key()}. */
@Component
public class ProviderRegistry {

    private final Map<String, DataProvider> providers;

    public ProviderRegistry(List<DataProvider> providers) {
        this.providers = providers.stream()
                .collect(Collectors.toUnmodifiableMap(DataProvider::key, Function.identity(), (first, second) -> {
                    throw new IllegalStateException("Duplicate data provider key '%s': %s and %s"
                            .formatted(
                                    first.key(),
                                    first.getClass().getName(),
                                    second.getClass().getName()));
                }));
    }

    public Optional<DataProvider> find(String key) {
        return Optional.ofNullable(providers.get(key));
    }

    public DataProvider require(String key) {
        return find(key)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown data provider '%s'. Known providers: %s".formatted(key, providers.keySet())));
    }

    public Collection<DataProvider> all() {
        return providers.values();
    }
}
