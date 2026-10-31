package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** External web site a data provider scrapes (e.g. OANDA, MyFXBook). */
@Entity
@Table(name = "site")
public class Site extends BaseEntity {

    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    protected Site() {}

    public Site(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
