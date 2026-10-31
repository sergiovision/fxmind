package com.fxmind.dto;

/** Outcome of one provider run; rendered as the job status in the admin console. */
public record IngestionResult(String provider, int fetched, int stored, int skipped) {

    public String summary() {
        return "%s: fetched %d, stored %d, skipped %d".formatted(provider, fetched, stored, skipped);
    }
}
