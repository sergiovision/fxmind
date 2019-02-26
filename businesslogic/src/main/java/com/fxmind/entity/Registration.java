package com.fxmind.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Sergei Zhuravlev
 */
@Embeddable
public class Registration implements Serializable {
    @Column(name = "UUID", columnDefinition = "longtext")
    private String uuid;

    @Column(name = "ACTIVATED", columnDefinition = "BIT")
    private Boolean activated;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false, unique = false)
    private Date created;

    @Column(name = "REG_IP", nullable = false, unique = false, columnDefinition = "longtext")
    private String regIp;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp;
    }
}
