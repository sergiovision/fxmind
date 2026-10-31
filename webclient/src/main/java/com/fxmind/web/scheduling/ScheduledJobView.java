package com.fxmind.web.scheduling;

import java.time.Instant;

/** Console read model combining a persistent job definition with its live Quartz state. */
public record ScheduledJobView(
        String group,
        String name,
        String handler,
        String description,
        String cron,
        boolean enabled,
        String state,
        boolean running,
        String lastStatus,
        Instant previousFireTime,
        Instant nextFireTime) {}
