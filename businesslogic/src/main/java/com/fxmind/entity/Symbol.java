package com.fxmind.entity;

import javax.persistence.*;

/**
 *
 * <p>Title: Symbol</p>
 *
 * <p>Description: Domain Object describing a Symbol entity</p>
 *
 */
@Entity (name="Symbol")
@Table (name="symbol")
@NamedQueries ({
	 @NamedQuery(name="Symbol.findAll", query="SELECT symbol FROM Symbol symbol")
	,@NamedQuery(name="Symbol.findByName", query="SELECT symbol FROM Symbol symbol WHERE symbol.name = :name")
	,@NamedQuery(name="Symbol.findByNameContaining", query="SELECT symbol FROM Symbol symbol WHERE symbol.name like :name")

	,@NamedQuery(name="Symbol.findByDescription", query="SELECT symbol FROM Symbol symbol WHERE symbol.description = :description")
	,@NamedQuery(name="Symbol.findByDescriptionContaining", query="SELECT symbol FROM Symbol symbol WHERE symbol.description like :description")

	,@NamedQuery(name="Symbol.findByDisabled", query="SELECT symbol FROM Symbol symbol WHERE symbol.disabled = :disabled")

	,@NamedQuery(name="Symbol.findByUse4tech", query="SELECT symbol FROM Symbol symbol WHERE symbol.use4tech = :use4tech")

	,@NamedQuery(name="Symbol.findByC1", query="SELECT symbol FROM Symbol symbol WHERE symbol.c1 = :c1")
	,@NamedQuery(name="Symbol.findByC1Containing", query="SELECT symbol FROM Symbol symbol WHERE symbol.c1 like :c1")

	,@NamedQuery(name="Symbol.findByC2", query="SELECT symbol FROM Symbol symbol WHERE symbol.c2 = :c2")
	,@NamedQuery(name="Symbol.findByC2Containing", query="SELECT symbol FROM Symbol symbol WHERE symbol.c2 like :c2")

})

public class Symbol extends AbstractEntity<Integer> {
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Symbol.findAll";
    public static final String FIND_BY_NAME = "Symbol.findByName";
    public static final String FIND_BY_NAME_CONTAINING ="Symbol.findByNameContaining";
    public static final String FIND_BY_DESCRIPTION = "Symbol.findByDescription";
    public static final String FIND_BY_DESCRIPTION_CONTAINING ="Symbol.findByDescriptionContaining";
    public static final String FIND_BY_DISABLED = "Symbol.findByDisabled";
    public static final String FIND_BY_USE4TECH = "Symbol.findByUse4tech";
    public static final String FIND_BY_C1 = "Symbol.findByC1";
    public static final String FIND_BY_C1_CONTAINING ="Symbol.findByC1Containing";
    public static final String FIND_BY_C2 = "Symbol.findByC2";
    public static final String FIND_BY_C2_CONTAINING ="Symbol.findByC2Containing";
	
    @Column(name="Name"  , length=50 , nullable=true , unique=true)
    private String name; 

    @Column(name="Description"  , length=500 , nullable=true , unique=false)
    private String description; 

    @Column(name="Disabled"   , nullable=true , unique=false)
    private Boolean disabled; 

    @Column(name="Use4Tech"   , nullable=true , unique=false)
    private Boolean use4tech; 

    @Column(name="C1"  , length=10 , nullable=true , unique=false)
    private String c1; 

    @Column(name="C2"  , length=10 , nullable=true , unique=false)
    private String c2; 

    public Symbol() {
    }

    public String getName() {
        return name;
    }
	
    public void setName (String name) {
        this.name =  name;
    }
	
    public String getDescription() {
        return description;
    }
	
    public void setDescription (String description) {
        this.description =  description;
    }
	
    public Boolean getDisabled() {
        return disabled;
    }
	
    public void setDisabled (Boolean disabled) {
        this.disabled =  disabled;
    }
	
    public Boolean getUse4tech() {
        return use4tech;
    }
	
    public void setUse4tech (Boolean use4tech) {
        this.use4tech =  use4tech;
    }
	
    public String getC1() {
        return c1;
    }
	
    public void setC1 (String c1) {
        this.c1 =  c1;
    }
	
    public String getC2() {
        return c2;
    }
	
    public void setC2 (String c2) {
        this.c2 =  c2;
    }

}
