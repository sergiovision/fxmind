package com.fxmind.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.Objects;
import org.hibernate.proxy.HibernateProxy;

/**
 * Identity base class for all aggregates.
 *
 * <p>Equality is identifier based and proxy-safe; {@link #hashCode()} is constant per concrete type so
 * that entities stay in the correct hash bucket across the transient → managed transition.
 */
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    public Long getId() {
        return id;
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || effectiveClass(this) != effectiveClass(other)) {
            return false;
        }
        var that = (BaseEntity) other;
        return id != null && Objects.equals(id, that.getId());
    }

    @Override
    public final int hashCode() {
        return effectiveClass(this).hashCode();
    }

    private static Class<?> effectiveClass(Object entity) {
        return entity instanceof HibernateProxy proxy
                ? proxy.getHibernateLazyInitializer().getPersistentClass()
                : entity.getClass();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id;
    }
}
