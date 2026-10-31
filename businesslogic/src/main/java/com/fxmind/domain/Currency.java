package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** ISO-4217 currency tracked by the platform (e.g. {@code EUR}). */
@Entity
@Table(name = "currency")
public class Currency extends BaseEntity {

    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled = true;

    protected Currency() {}

    public Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
