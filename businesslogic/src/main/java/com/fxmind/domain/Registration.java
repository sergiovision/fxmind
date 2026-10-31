package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Instant;

/** Self-registration details embedded in {@link Person}. */
@Embeddable
public class Registration {

    @Column(name = "ACTIVATION_TOKEN", length = 64, unique = true)
    private String activationToken;

    @Column(name = "ACTIVATED", nullable = false)
    private boolean activated;

    @Column(name = "REGISTERED_AT", nullable = false)
    private Instant registeredAt;

    @Column(name = "REGISTRATION_IP", length = 45)
    private String registrationIp;

    protected Registration() {}

    public Registration(String activationToken, Instant registeredAt, String registrationIp) {
        this.activationToken = activationToken;
        this.registeredAt = registeredAt;
        this.registrationIp = registrationIp;
    }

    public static Registration preActivated(Instant registeredAt) {
        var registration = new Registration(null, registeredAt, null);
        registration.activated = true;
        return registration;
    }

    public String getActivationToken() {
        return activationToken;
    }

    /** Activates the account and invalidates the one-time token so the mail link cannot be replayed. */
    public void consumeToken() {
        activationToken = null;
        activated = true;
    }

    public boolean isActivated() {
        return activated;
    }

    /**
     * Administrative override of the activation state. Any explicit decision also invalidates the mail
     * token, so an old activation link can never undo it.
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
        this.activationToken = null;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public String getRegistrationIp() {
        return registrationIp;
    }
}
