package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;

/** Runtime-editable key/value setting with a factory default that can be restored. */
@Entity
@Table(name = "settings")
public class Setting extends BaseEntity {

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "SETTING_VALUE", columnDefinition = "TEXT")
    private String value;

    @Column(name = "DEFAULT_VALUE", columnDefinition = "TEXT")
    private String defaultValue;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    protected Setting() {}

    public Setting(String name, String value, String description) {
        this.name = Objects.requireNonNull(name, "name");
        this.value = value;
        this.defaultValue = value;
        this.description = description;
    }

    public boolean resetToDefault() {
        if (defaultValue == null || defaultValue.equals(value)) {
            return false;
        }
        value = defaultValue;
        return true;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDescription() {
        return description;
    }
}
