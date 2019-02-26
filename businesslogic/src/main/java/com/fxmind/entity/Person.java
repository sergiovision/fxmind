package com.fxmind.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>Title: Person</p>
 *
 * @author Sergei Zhuravlev
 *         <p>Description: Domain Object describing a Person entity</p>
 */
@Entity(name = "Person")
@Table(name = "person")
@NamedQueries({
        @NamedQuery(name = "Person.findByMail", query = "SELECT person FROM Person person WHERE person.mail = :mail"),
        @NamedQuery(name = "Person.findByUuid", query = "SELECT person FROM Person person WHERE person.registration.uuid = :uuid"),
        @NamedQuery(name = "Person.findById", query = "SELECT person FROM Person person WHERE person.id = :id")
})
public class Person extends AbstractEntity<Integer> {
    private static final long serialVersionUID = 19898L;

    public static final String FIND_BY_MAIL = "Person.findByMail";
    public static final String FIND_BY_UUID = "Person.findByUuid";
    public static final String FIND_BY_ID = "Person.findById";

    @Column(name = "BALANCE", nullable = false, unique = false)
    private BigDecimal balance;

    @Column(name = "LANGUAGE_ID", nullable = false, unique = false, columnDefinition = "mediumint")
    private Integer languageId;

    @Column(name = "CREDENTIAL", nullable = false, unique = false, columnDefinition = "longtext")
    private String credential;

    @Column(name = "MAIL", nullable = false, unique = true)
    private String mail;

    @Column(name = "FACEBOOK_ID", nullable = true, unique = true)
    private Long facebookId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COUNTRY_ID")
    private Country country;

    @Column(name = "PRIVILEGE")
    @Enumerated(EnumType.STRING)
    private Privilege privilege;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SUBSRIPTION_EXPIRE_DATE", nullable = true, unique = false)
    private Date subcriptionExpireDate;

    @Embedded
    private Registration registration;

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

    public Long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public Date getSubcriptionExpireDate() {
        return subcriptionExpireDate;
    }

    public void setSubcriptionExpireDate(Date subcriptionExpireDate) {
        this.subcriptionExpireDate = subcriptionExpireDate;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Person)) return false;
        if (!super.equals(object)) return false;

        Person person = (Person) object;

        if (balance != null ? !balance.equals(person.getBalance()) : person.getBalance() != null) return false;
        if (country != null ? !country.equals(person.getCountry()) : person.getCountry() != null) return false;
        if (credential != null ? !credential.equals(person.getCredential()) : person.getCredential() != null) return false;
        if (facebookId != null ? !facebookId.equals(person.getFacebookId()) : person.getFacebookId() != null) return false;
        if (languageId != null ? !languageId.equals(person.getLanguageId()) : person.getLanguageId() != null) return false;
        if (mail != null ? !mail.equals(person.getMail()) : person.getMail() != null) return false;
        if (subcriptionExpireDate != null ? !subcriptionExpireDate.equals(person.getSubcriptionExpireDate()) :
                person.getSubcriptionExpireDate() != null) return false;
        if (privilege != person.getPrivilege()) return false;
        if (registration != null ? !registration.equals(person.getRegistration()) : person.getRegistration() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (languageId != null ? languageId.hashCode() : 0);
        result = 31 * result + (credential != null ? credential.hashCode() : 0);
        result = 31 * result + (mail != null ? mail.hashCode() : 0);
        result = 31 * result + (facebookId != null ? facebookId.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (privilege != null ? privilege.hashCode() : 0);
        result = 31 * result + (subcriptionExpireDate != null ? subcriptionExpireDate.hashCode() : 0);
        result = 31 * result + (registration != null ? registration.hashCode() : 0);
        return result;
    }

    public static enum Privilege {
        FREE,
        PREMIUM,
        ADMIN
    }
}
