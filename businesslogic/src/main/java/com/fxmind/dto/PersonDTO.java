package com.fxmind.dto;

import com.fxmind.entity.Person;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Sergei Zhuravlev
 */
public class PersonDTO implements Serializable {
    private Integer id;
    private BigDecimal balance;
    private Integer languageId;
    private String credential;
    private String mail;
    private Person.Privilege privilege;
    private String uuid;
    private Boolean activated;
    private Date created;
    private Date subcriptionExpireDate;
    private String regIp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Person.Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Person.Privilege privilege) {
        this.privilege = privilege;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getActivated() {
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

    public Date getSubcriptionExpireDate() {
        return subcriptionExpireDate;
    }

    public void setSubcriptionExpireDate(Date subcriptionExpireDate) {
        this.subcriptionExpireDate = subcriptionExpireDate;
    }
}
