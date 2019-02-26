package com.fxmind.entity;

import javax.persistence.*;

/**
 * <p>Title: Country</p>
 *
 * @author Sergei Zhuravlev
 *         <p>Description: Domain Object describing a Country entity</p>
 */
@Entity(name = "Country")
@Table(name = "country")
public class Country extends AbstractEntity<Integer> {
    private static final long serialVersionUID = 1879879L;

    @Column(name = "NAME_ID", nullable = false, unique = false, columnDefinition = "mediumint")
    private Integer nameId;

    @Column(name = "ALPHA2", length = 2, nullable = false, unique = true)
    private String alpha2;

    @Column(name = "IS_ISO3166", nullable = false, unique = true, columnDefinition = "BIT")
    private Boolean iso3166;

    @ManyToOne(fetch= FetchType.EAGER , optional=false)
    @JoinColumn(name="CURRENCYID", referencedColumnName = "ID" , nullable=true, unique=false , insertable=true, updatable=true)
    private Currency currencyid;

    public Currency getCurrencyid() {
        return currencyid;
    }

    public void setCurrencyid(Currency currencyid) {
        this.currencyid = currencyid;
    }

    public Integer getNameId() {
        return nameId;
    }

    public void setNameId(Integer nameId) {
        this.nameId = nameId;
    }

    public String getAlpha2() {
        return alpha2;
    }

    public void setAlpha2(String alpha2) {
        this.alpha2 = alpha2;
    }

    public Boolean isIso3166() {
        return iso3166;
    }

    public void setIso3166(Boolean iso3166) {
        this.iso3166 = iso3166;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Country)) return false;
        if (!super.equals(object)) return false;

        Country country = (Country) object;

        if (alpha2 != null ? !alpha2.equals(country.getAlpha2()) : country.getAlpha2() != null) return false;
        if (iso3166 != null ? !iso3166.equals(country.isIso3166()) : country.isIso3166() != null) return false;
        if (nameId != null ? !nameId.equals(country.getNameId()) : country.getNameId() != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (nameId != null ? nameId.hashCode() : 0);
        result = 31 * result + (alpha2 != null ? alpha2.hashCode() : 0);
        result = 31 * result + (iso3166 != null ? iso3166.hashCode() : 0);
        return result;
    }
}
