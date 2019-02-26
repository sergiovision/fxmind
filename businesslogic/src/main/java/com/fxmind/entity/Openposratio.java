package com.fxmind.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 *
 * <p>Title: Openposratio</p>
 *
 * <p>Description: Domain Object describing a Openposratio entity</p>
 *
 */
@Entity (name="Openposratio")
@Table (name="openposratio")
@NamedQueries ({
	 @NamedQuery(name="Openposratio.findAll", query="SELECT openposratio FROM Openposratio openposratio")
	,@NamedQuery(name="Openposratio.findByParsetime", query="SELECT openposratio FROM Openposratio openposratio WHERE openposratio.parsetime = :parsetime")

	,@NamedQuery(name="Openposratio.findBySymbolid", query="SELECT openposratio FROM Openposratio openposratio WHERE openposratio.symbolid = :symbolid")

	,@NamedQuery(name="Openposratio.findByLongratio", query="SELECT openposratio FROM Openposratio openposratio WHERE openposratio.longratio = :longratio")

	,@NamedQuery(name="Openposratio.findByShortratio", query="SELECT openposratio FROM Openposratio openposratio WHERE openposratio.shortratio = :shortratio")

	,@NamedQuery(name="Openposratio.findBySiteid", query="SELECT openposratio FROM Openposratio openposratio WHERE openposratio.siteid = :siteid")

})

public class Openposratio extends AbstractEntity<Integer> {
    private static final long serialVersionUID = 1L;

    public static final String FIND_ALL = "Openposratio.findAll";
    public static final String FIND_BY_PARSETIME = "Openposratio.findByParsetime";
    public static final String FIND_BY_SYMBOLID = "Openposratio.findBySymbolid";
    public static final String FIND_BY_LONGRATIO = "Openposratio.findByLongratio";
    public static final String FIND_BY_SHORTRATIO = "Openposratio.findByShortratio";
    public static final String FIND_BY_SITEID = "Openposratio.findBySiteid";
	
    //@Id @Column(name="ID" )
    //@GeneratedValue(strategy = GenerationType.AUTO)
    //private Integer id;

    @Column(name="ParseTime"   , nullable=false , unique=true)
    private Timestamp parsetime;

    @Column(name="SymbolID"   , nullable=true , unique=true)
    private Integer symbolid; 

    @Column(name="LongRatio"   , nullable=true , unique=false)
    private Double longratio;

    @Column(name="ShortRatio"   , nullable=true , unique=false)
    private Double shortratio;

    @Column(name="SiteID"   , nullable=true , unique=false)
    private Integer siteid;

    /**
    * Default constructor
    */
    public Openposratio() {
    }


    public Timestamp getParsetime() {
        return parsetime;
    }
	
    public void setParsetime (Timestamp parsetime) {
        this.parsetime =  parsetime;
    }

    public Integer getSymbolid() {
        return symbolid;
    }
	
    public void setSymbolid (Integer symbolid) {
        this.symbolid =  symbolid;
    }
	
    public Double getLongratio() {
        return longratio;
    }
	
    public void setLongratio (Double longratio) {
        this.longratio =  longratio;
    }
	
    public Double getShortratio() {
        return shortratio;
    }
	
    public void setShortratio (Double shortratio) {
        this.shortratio =  shortratio;
    }
	
    public Integer getSiteid() {
        return siteid;
    }
	
    public void setSiteid (Integer siteid) {
        this.siteid =  siteid;
    }

}
