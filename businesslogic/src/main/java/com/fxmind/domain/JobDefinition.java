package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Objects;

/**
 * Persistent description of a scheduled job. The scheduler keeps Quartz in sync with these rows, so
 * jobs can be added, re-scheduled or disabled at runtime from the admin console.
 */
@Entity
@Table(
        name = "jobs",
        uniqueConstraints =
                @UniqueConstraint(
                        name = "UK_JOBS_GROUP_NAME",
                        columnNames = {"GRP", "NAME"}))
public class JobDefinition extends BaseEntity {

    public static final int MAX_STATUS_LENGTH = 4000;

    @Column(name = "GRP", nullable = false)
    private String group;

    @Column(name = "NAME", nullable = false)
    private String name;

    /** Key of the {@code DataProvider} (or system task) executed by this job. */
    @Column(name = "HANDLER", nullable = false, length = 100)
    private String handler;

    @Column(name = "CRON", nullable = false, length = 128)
    private String cron;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "LAST_STATUS", columnDefinition = "TEXT")
    private String lastStatus;

    @Column(name = "PREVIOUS_FIRE_TIME")
    private Instant previousFireTime;

    @Column(name = "NEXT_FIRE_TIME")
    private Instant nextFireTime;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled = true;

    protected JobDefinition() {}

    public JobDefinition(String group, String name, String handler, String cron, String description) {
        this.group = Objects.requireNonNull(group, "group");
        this.name = Objects.requireNonNull(name, "name");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.cron = Objects.requireNonNull(cron, "cron");
        this.description = description;
    }

    public void recordExecution(String status, Instant firedAt, Instant nextFire) {
        this.lastStatus = status == null || status.length() <= MAX_STATUS_LENGTH
                ? status
                : status.substring(0, MAX_STATUS_LENGTH);
        this.previousFireTime = firedAt;
        this.nextFireTime = nextFire;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getHandler() {
        return handler;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = Objects.requireNonNull(cron, "cron");
    }

    public String getDescription() {
        return description;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public Instant getPreviousFireTime() {
        return previousFireTime;
    }

    public Instant getNextFireTime() {
        return nextFireTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
