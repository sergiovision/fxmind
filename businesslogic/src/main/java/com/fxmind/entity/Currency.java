package com.fxmind.entity;

import javax.persistence.*;

/**
 *
 * <p>Title: Currency</p>
 *
 * <p>Description: Domain Object describing a Currency entity</p>
 *
 */
@Entity (name="Currency")
@Table (name="currency")
@NamedQueries ({
	 @NamedQuery(name="Currency.findAll", query="SELECT currency FROM Currency currency")
	,@NamedQuery(name="Currency.findByName", query="SELECT currency FROM Currency currency WHERE currency.name = :name")
	,@NamedQuery(name="Currency.findByNameContaining", query="SELECT currency FROM Currency currency WHERE currency.name like :name")

	,@NamedQuery(name="Currency.findByEnabled", query="SELECT currency FROM Currency currency WHERE currency.enabled = :enabled")

})

public class Currency  extends AbstractEntity<Short> {
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Currency.findAll";
    public static final String FIND_BY_NAME = "Currency.findByName";
    public static final String FIND_BY_NAME_CONTAINING ="Currency.findByNameContaining";
    public static final String FIND_BY_ENABLED = "Currency.findByEnabled";

    @Column(name="Name"  , length=20 , nullable=false , unique=true)
    private String name; 

    @Column(name="Enabled"   , nullable=true , unique=false)
    private Boolean enabled;

    public String getName() {
        return name;
    }
	
    public void setName (String name) {
        this.name =  name;
    }
	
    public Boolean getEnabled() {
        return enabled;
    }
	
    public void setEnabled (Boolean enabled) {
        this.enabled =  enabled;
    }

}
