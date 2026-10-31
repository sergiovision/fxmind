package com.fxmind.domain;

/** Access level of a {@link Person}; maps 1:1 to a Spring Security role. */
public enum Privilege {
    FREE,
    PREMIUM,
    ADMIN;

    public String authority() {
        return "ROLE_" + name();
    }
}
