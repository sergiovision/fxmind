package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/** Platform user. Passwords are stored as BCrypt hashes only. */
@Entity
@Table(name = "person")
public class Person extends BaseEntity {

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRIVILEGE", nullable = false, length = 20)
    private Privilege privilege = Privilege.FREE;

    @Column(name = "BALANCE", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "SUBSCRIPTION_EXPIRES_AT")
    private Instant subscriptionExpiresAt;

    @Embedded
    private Registration registration;

    protected Person() {}

    public Person(String email, String passwordHash, Privilege privilege, Registration registration) {
        this.email = Objects.requireNonNull(email, "email");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.privilege = Objects.requireNonNull(privilege, "privilege");
        this.registration = Objects.requireNonNull(registration, "registration");
    }

    public void activate() {
        registration.consumeToken();
    }

    public boolean isActivated() {
        return registration.isActivated();
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = Objects.requireNonNull(privilege, "privilege");
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = Objects.requireNonNull(balance, "balance");
    }

    public Instant getSubscriptionExpiresAt() {
        return subscriptionExpiresAt;
    }

    public void setSubscriptionExpiresAt(Instant subscriptionExpiresAt) {
        this.subscriptionExpiresAt = subscriptionExpiresAt;
    }

    public Registration getRegistration() {
        return registration;
    }
}
